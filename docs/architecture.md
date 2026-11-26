# 아키텍처

기준 패키지는 `study.posts`다. Spring Boot를 시작하면 Spring이 입력 포트와 출력 포트에 각각 구현을 주입한다.

| 패키지 | 책임 | 허용하는 참조 |
| --- | --- | --- |
| `domain` | `Post`로 게시글의 값을 표현 | Java 표준 라이브러리 |
| `application` | 작성·조회 계약을 입력 포트로 정의하고, 구현에서 `PostRepository`를 통해 게시글을 저장·조회 | 같은 계층의 포트, `domain`, Spring의 빈 등록 |
| `adapter.web` | `PostController`에서 작성 요청을 검증하고 작성·조회 HTTP 응답 반환 | `application`의 `CreatePostUseCase`와 `ListPostsUseCase`, `domain`, Spring MVC, Jakarta Validation |
| `adapter.persistence` | `InMemoryPostRepository`에서 ID를 발급하고 게시글을 메모리에 저장·조회 | `application`의 `PostRepository`, `domain`, Spring의 빈 등록 |

## POST /posts 요청 흐름

1. [PostController](../src/main/java/study/posts/adapter/web/PostController.java)가 JSON의 `title`과 `content`를 검증한다. 누락, 빈 문자열, 공백만 있는 값은 HTTP 400으로 처리한다.
2. 유효한 요청을 입력 포트 [CreatePostUseCase](../src/main/java/study/posts/application/CreatePostUseCase.java)로 전달한다. Spring이 연결한 [CreatePost](../src/main/java/study/posts/application/CreatePost.java)가 요청을 처리한다.
3. `CreatePost`는 출력 포트 [PostRepository](../src/main/java/study/posts/application/PostRepository.java)에 저장을 위임한다. 이 포트를 구현한 [InMemoryPostRepository](../src/main/java/study/posts/adapter/persistence/InMemoryPostRepository.java)가 ID를 발급하고 `Post`를 저장한다.
4. 저장된 `Post`가 컨트롤러로 돌아오면 HTTP 201과 `id`, `title`, `content`를 반환한다.

## GET /posts 요청 흐름

1. `PostController`가 조회 입력 포트 `ListPostsUseCase`를 호출한다.
2. `ListPosts`가 `PostRepository`로 게시글 목록을 가져와 ID 오름차순으로 정렬한다. `InMemoryPostRepository`는 저장된 게시글 목록을 복사해 반환한다.
3. 컨트롤러에서 `id`, `title`, `content`를 목록 응답으로 변환한다. HTTP 200과 JSON 배열을 반환하며 목록이 비어 있으면 `[]`를 반환한다.

## ArchUnit 규칙

[ArchitectureTest](../src/test/java/study/posts/architecture/ArchitectureTest.java)는 `@AnalyzeClasses`와 `DoNotIncludeTests`를 사용해 운영 코드의 컴파일된 의존성을 검사한다. [Loiane Groner의 ArchUnit 예제](https://loiane.com/2026/07/architecture-testing-java-archunit/)

1. `domain`은 Spring, `application`, `adapter`에 의존하지 않는다.
2. `application`은 `adapter`에 의존하지 않는다.
3. `adapter.web`은 `adapter.persistence`에 직접 의존하지 않는다.
4. `adapter.web`이 참조할 수 있는 `application` 타입은 입력 포트 `CreatePostUseCase`와 `ListPostsUseCase`뿐이다. 유스케이스 구현과 `PostRepository`를 직접 참조하면 실패한다.
