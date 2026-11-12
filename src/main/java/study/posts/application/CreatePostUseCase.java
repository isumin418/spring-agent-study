package study.posts.application;

import study.posts.domain.Post;

public interface CreatePostUseCase {
    Post create(String title, String content);
}
