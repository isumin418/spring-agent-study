package study.posts.adapter.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import study.posts.application.CreatePostUseCase;
import study.posts.application.ListPostsUseCase;
import study.posts.domain.Post;

@RestController
public class PostController {
    private final CreatePostUseCase createPost;
    private final ListPostsUseCase listPosts;

    public PostController(CreatePostUseCase createPost, ListPostsUseCase listPosts) {
        this.createPost = createPost;
        this.listPosts = listPosts;
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@Valid @RequestBody CreatePostRequest request) {
        return createPost.create(request.title(), request.content());
    }

    @GetMapping("/posts")
    public List<PostListItem> list() {
        return listPosts.list().stream()
                .map(post -> new PostListItem(post.id(), post.title(), post.content()))
                .toList();
    }

    public record CreatePostRequest(@NotBlank String title, @NotBlank String content) {
    }

    public record PostListItem(long id, String title, String content) {
    }
}
