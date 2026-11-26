package study.posts.application;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import study.posts.domain.Post;

@Service
public class ListPosts implements ListPostsUseCase {
    private final PostRepository repository;

    public ListPosts(PostRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Post> list() {
        return repository.findAll().stream()
                .sorted(Comparator.comparingLong(Post::id))
                .toList();
    }
}
