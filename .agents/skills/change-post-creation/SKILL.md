---
name: change-post-creation
description: POST /posts의 HTTP 요청·응답 계약이나 입력 검증, 게시글 생성 규칙, 저장 계약이나 구현을 변경할 때 사용한다. README 문구 수정이나 Hook 설정만 변경하는 작업에는 적용하지 않는다.
---

1. [아키텍처 문서](../../../docs/architecture.md)에서 의존성 규칙과 작성 흐름을 확인한다.
2. 요청 종류에 따라 먼저 읽을 파일과 변경할 계층을 정한다.

   | 요청 종류 | 먼저 읽을 파일 | 변경할 계층 |
   | --- | --- | --- |
   | HTTP 요청·응답 변경 | `src/main/java/study/posts/adapter/web/PostController.java` | `adapter.web`에서 요청 레코드, HTTP 검증과 응답을 수정한다. 작성 요청은 `CreatePostUseCase`로 전달한다. |
   | 게시글 생성 규칙 변경 | `src/main/java/study/posts/application/CreatePost.java`, `src/main/java/study/posts/domain/Post.java` | 작성 과정의 규칙은 `application`, 게시글 자체가 항상 지켜야 하는 규칙은 `domain`에 둔다. 작성 호출 계약이 바뀌면 `src/main/java/study/posts/application/CreatePostUseCase.java`도 확인한다. |
   | 저장 방식 변경 | `src/main/java/study/posts/application/PostRepository.java`, `src/main/java/study/posts/adapter/persistence/InMemoryPostRepository.java` | 저장 방식은 `adapter.persistence`에서 바꾼다. 유스케이스에 필요한 저장 계약이 바뀔 때 `application`의 출력 포트를 수정한다. |

3. 코드를 수정한다. HTTP 동작이 바뀌면 `src/test/java/study/posts/adapter/web/PostControllerTest.java`에서 해당 동작을 검증한다.
4. 완료 전에 프로젝트 루트에서 Windows는 `.\gradlew.bat test`, macOS·Linux는 `sh ./gradlew test`를 실행한다. 전체 테스트에 `PostControllerTest`와 `ArchitectureTest`가 포함된다.
