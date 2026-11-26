package study.posts.application;

import java.util.List;
import study.posts.domain.Post;

public interface PostRepository {
    Post save(String title, String content);

    List<Post> findAll();
}
