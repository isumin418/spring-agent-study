package study.posts.application;

import org.springframework.stereotype.Service;
import study.posts.domain.Post;

@Service
public class CreatePost implements CreatePostUseCase {
    private final PostRepository repository;

    public CreatePost(PostRepository repository) {
        this.repository = repository;
    }

    @Override
    public Post create(String title, String content) {
        return repository.save(title, content);
    }
}
