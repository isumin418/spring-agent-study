package study.posts.adapter.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import study.posts.application.PostRepository;
import study.posts.domain.Post;

@Repository
public class InMemoryPostRepository implements PostRepository {
    private final AtomicLong sequence = new AtomicLong();
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();

    @Override
    public Post save(String title, String content) {
        Post post = new Post(sequence.incrementAndGet(), title, content);
        posts.put(post.id(), post);
        return post;
    }
}
