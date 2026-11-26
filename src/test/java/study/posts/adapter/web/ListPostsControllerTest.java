package study.posts.adapter.web;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import study.posts.adapter.persistence.InMemoryPostRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class ListPostsControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private InMemoryPostRepository repository;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void returnsEmptyArrayWhenNoPostsExist() throws Exception {
        mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void returnsPostsInAscendingIdOrder() throws Exception {
        // Register a higher ID first while still using the real POST and storage paths.
        AtomicLong sequence = (AtomicLong) ReflectionTestUtils.getField(repository, "sequence");
        sequence.set(16);
        mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Higher ID","content":"Registered first"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(17));

        sequence.set(0);
        mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Lower ID","content":"Registered second"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {"id":1,"title":"Lower ID","content":"Registered second"},
                          {"id":17,"title":"Higher ID","content":"Registered first"}
                        ]
                        """, JsonCompareMode.STRICT));
    }
}
