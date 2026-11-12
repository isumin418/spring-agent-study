package study.posts.adapter.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import study.posts.application.CreatePostUseCase;
import study.posts.domain.Post;

@RestController
public class PostController {
    private final CreatePostUseCase createPost;

    public PostController(CreatePostUseCase createPost) {
        this.createPost = createPost;
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@Valid @RequestBody CreatePostRequest request) {
        return createPost.create(request.title(), request.content());
    }

    public record CreatePostRequest(@NotBlank String title, @NotBlank String content) {
    }
}
