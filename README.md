# spring-agent-study

Spring Boot 게시글 API로 클린 아키텍처와 AI 에이전트 개발 흐름을 실습하는 프로젝트다. 게시글 작성 기능을 기반으로 목록 조회를 추가하면서 **OpenSpec 명세 작성 → 구현 전 테스트 실패 확인 → 구현 → 기능·계층 검증 → 변경 보관**을 진행했다.

## 실행

Java 21, Spring Boot 4.0.8, Gradle Wrapper 9.6.0을 사용한다. 서버 기본 주소는 `http://localhost:8080`이다.

| 작업 | Windows PowerShell | macOS·Linux |
| --- | --- | --- |
| 서버 실행 | `.\gradlew.bat bootRun` | `sh ./gradlew bootRun` |
| 전체 테스트 | `.\gradlew.bat test` | `sh ./gradlew test` |
| ArchUnit만 실행 | `.\gradlew.bat test --tests '*ArchitectureTest'` | `sh ./gradlew test --tests '*ArchitectureTest'` |

게시글은 애플리케이션 메모리에 저장되며 프로세스를 종료하면 사라진다.

## 게시글 API

| 요청 | 응답 |
| --- | --- |
| `POST /posts` | `title`, `content`를 받아 저장하고 `201 Created`와 `id`, `title`, `content`를 반환한다. 제목이나 본문이 누락됐거나 빈 문자열 또는 공백뿐이면 `400 Bad Request`를 반환한다. |
| `GET /posts` | `200 OK`와 래퍼 없는 JSON 배열을 반환한다. 각 항목은 `id`, `title`, `content`를 포함하며 ID 오름차순으로 정렬된다. 저장된 글이 없으면 `[]`를 반환한다. |

게시글 작성 요청 예시

```json
{"title":"첫 글","content":"안녕하세요"}
```

목록 조회 응답 예시

```json
[
  {"id":1,"title":"첫 글","content":"안녕하세요"}
]
```

## 아키텍처

`PostController`는 작성·조회 입력 포트로 요청을 전달한다. 각 유스케이스는 `PostRepository`를 통해 같은 인메모리 저장소를 사용한다. 조회 유스케이스는 게시글을 ID 오름차순으로 정렬하고, 컨트롤러는 목록 응답에 공개할 필드를 명시적으로 매핑한다.

```text
PostController → CreatePostUseCase → CreatePost ─┐
                                                 ├→ PostRepository → InMemoryPostRepository
PostController → ListPostsUseCase  → ListPosts ──┘
```

[패키지 책임과 의존성 규칙](docs/architecture.md)

## AI 에이전트 개발 실습

| 구성 | 적용한 이유 |
| --- | --- |
| [`AGENTS.md`](AGENTS.md) | Codex가 수정 전에 확인할 문서와 작업 후 실행할 테스트를 찾을 수 있게 한다. |
| [게시글 작성 Skill](.agents/skills/change-post-creation/SKILL.md) | 작성 API의 계약, 검증, 저장 동작을 변경할 때 참고할 절차를 제공한다. |
| [OpenSpec](openspec/specs/post-list/spec.md) | 목록 조회의 빈 배열 응답과 ID 정렬을 구현 전에 명시하고, 각 조건을 MockMvc 테스트로 확인한다. |
| [ArchUnit](src/test/java/study/posts/architecture/ArchitectureTest.java) | 웹 계층에서 저장소 구현체를 직접 참조하는 경우처럼 API 응답만으로는 드러나지 않는 계층 위반을 검사한다. |
| [Codex Stop Hook](.codex/hooks.json) | Codex 턴 종료 시 ArchUnit을 실행해 계층 검사 누락을 줄인다. 프로젝트 Hook은 Codex에서 신뢰를 승인한 뒤 실행된다. |


## 검증 결과

- **구현 전** — 목록 MockMvc 테스트 2개가 `200`을 기대했지만 GET 매핑이 없어 `405 Method Not Allowed`로 실패했다.
- **구현 후** — 목록 조회 2개, 기존 작성 API 9개, ArchUnit 4개로 **전체 15개 테스트가 통과했다.** 정렬 테스트는 높은 ID의 글을 먼저 등록한 뒤 낮은 ID의 글이 먼저 반환되는지 확인한다.
- **실패 탐지** — 임시 사본에서 정렬을 제거하면 목록 테스트가 실패했고, 컨트롤러가 인메모리 저장소 구현체를 직접 참조하면 ArchUnit이 실패했다.
- **Hook 자동 실행** — 신뢰 승인 후 별도 Codex CLI 턴을 종료해 자동 실행을 확인했다. Stop Hook이 생성한 리포트에서 **ArchUnit 4개가 통과했다.**
