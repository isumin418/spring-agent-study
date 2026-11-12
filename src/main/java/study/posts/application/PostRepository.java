package study.posts.application;

import study.posts.domain.Post;

public interface PostRepository {
    Post save(String title, String content);
}
