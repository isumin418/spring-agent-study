package study.posts.adapter.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void createsPost() throws Exception {
        mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"First post","content":"Hello"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("First post"))
                .andExpect(jsonPath("$.content").value("Hello"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"content\":\"Hello\"}",
            "{\"title\":\"First post\"}",
            "{\"title\":null,\"content\":\"Hello\"}",
            "{\"title\":\"First post\",\"content\":null}",
            "{\"title\":\"\",\"content\":\"Hello\"}",
            "{\"title\":\"First post\",\"content\":\"\"}",
            "{\"title\":\"   \",\"content\":\"Hello\"}",
            "{\"title\":\"First post\",\"content\":\"   \"}"
    })
    void rejectsInvalidPost(String body) throws Exception {
        mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
