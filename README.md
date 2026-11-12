# spring-agent-study

게시글 작성 API로 클린 아키텍처의 패키지 경계를 실습한다. Markdown에 작업 규칙과 설계를 기록하고, Codex Skill로 변경 절차를 안내한다. ArchUnit과 Hook으로 코드가 설계 방향을 따르는지 검사한다.

## 패키지 구조

| 패키지 | 열어볼 클래스 | 역할 |
| --- | --- | --- |
| `domain` | `Post` | 게시글 값 |
| `application` | `CreatePostUseCase`, `CreatePost`, `PostRepository` | 입력 포트, 작성 구현, 출력 포트 |
| `adapter.web` | `PostController` | HTTP 요청 검증과 응답 |
| `adapter.persistence` | `InMemoryPostRepository` | ID 발급과 인메모리 저장 |

`POST /posts`의 JSON 예시는 `{"title":"첫 글","content":"안녕하세요"}`다. 컨트롤러가 누락, 빈 문자열, 공백만 있는 입력을 검사해 400으로 응답한다. 유효하면 `PostController → CreatePostUseCase → CreatePost → PostRepository → InMemoryPostRepository` 순서로 호출한다. Spring이 연결한 인메모리 저장소가 ID를 발급해 저장하면 HTTP 201과 `id`, `title`, `content`가 반환된다. 애플리케이션을 종료하면 저장된 글은 사라진다.

## 실행과 테스트

JDK 21을 설치하고 `JAVA_HOME`이나 `PATH`를 설정한 뒤 프로젝트 루트에서 실행한다. 서버 기본 주소는 `http://localhost:8080`이다.

| 작업 | Windows PowerShell | macOS·Linux |
| --- | --- | --- |
| 서버 시작 | `.\gradlew.bat bootRun` | `sh ./gradlew bootRun` |
| 전체 테스트 | `.\gradlew.bat test` | `sh ./gradlew test` |
| ArchUnit만 실행 | `.\gradlew.bat test --tests '*ArchitectureTest'` | `sh ./gradlew test --tests '*ArchitectureTest'` |

[gradle.properties](gradle.properties)는 Windows에서 한글 경로를 처리할 수 있도록 Gradle JVM의 인코딩을 시스템 인코딩에 맞춘다. OneDrive에서 Gradle이 이전 빌드 출력을 지우지 못하면 PowerShell에서 아래 변수를 설정한 뒤 같은 명령을 실행한다. 이 설정을 Hook에도 적용하려면 같은 환경에서 Codex를 시작한다.

```powershell
$env:ORG_GRADLE_PROJECT_buildOutput = Join-Path $env:LOCALAPPDATA 'SpringBootCleanArchitectureLab/build'
.\gradlew.bat test
```

기본 테스트 보고서는 [Gradle HTML 리포트](build/reports/tests/test/index.html)에 생성된다. 출력 경로를 바꿨다면 그 경로 아래의 `reports/tests/test/index.html`을 연다. `PostControllerTest`는 Spring MVC를 통해 201 및 400 응답을 확인한다. `ArchitectureTest`는 [아키텍처 문서](docs/architecture.md)의 의존성 규칙 네 가지를 검사한다.

전체 테스트는 API 9개와 ArchUnit 4개로 구성된다.

이전 검증에서 임시 사본의 `CreatePost`와 `PostController`에 `InMemoryPostRepository` 직접 참조를 각각 추가하자 두 사본 모두 컴파일과 API 테스트는 통과했고, 각각 2번과 3번 ArchUnit 규칙만 실패했다.

## Codex 설정 파일

| 파일 | 역할 |
| --- | --- |
| [`AGENTS.md`](AGENTS.md) | Codex가 항상 지킬 작업 규칙과 기본 개발 명령 |
| [`docs/architecture.md`](docs/architecture.md) | 패키지별 책임, 의존성 방향, 게시글 작성 흐름 |
| [`.agents/skills/change-post-creation/SKILL.md`](.agents/skills/change-post-creation/SKILL.md) | HTTP 계약, 생성 규칙, 저장 방식의 변경 위치와 검증 절차 |
| [`.codex/hooks.json`](.codex/hooks.json) | Codex 이벤트와 Hook 명령 연결 |
| [`scripts/check-architecture.ps1`](scripts/check-architecture.ps1) | 프로젝트 루트에서 Gradle Wrapper로 ArchUnit 실행 |

## Codex Hook

Codex가 응답을 마칠 때 `Stop` 이벤트 → `scripts/check-architecture.ps1` → `ArchitectureTest` 순으로 실행된다. 스크립트는 자신의 디렉터리에서 프로젝트 루트를 찾아 그 위치의 Gradle Wrapper에 `test --tests "*ArchitectureTest"`를 전달한다. 프로젝트 설정을 신뢰한 뒤 CLI의 `/hooks` 또는 데스크톱의 **Review hooks**에서 명령을 검토하고 승인한다. Hook 변경 후 재검토가 표시되면 같은 메뉴에서 승인한다. JDK 21이 설정된 환경에서 Codex를 실행한다. Windows에서는 Windows PowerShell을, macOS·Linux에서는 PowerShell 7(`pwsh`)을 사용한다. [Codex Hook 공식 문서](https://developers.openai.com/codex/hooks)

Gradle이 실패하면 스크립트가 종료 코드 2로 Codex에 수정과 재확인을 요청한다. 같은 턴에서 Hook으로 이미 이어진 경우에는 반복 실행을 건너뛴다. 직접 확인하려면 Windows PowerShell에서 아래 명령을 실행한다.

```powershell
'{"stop_hook_active":false}' | powershell.exe -NoProfile -File scripts/check-architecture.ps1
```

이전 구성에서 확인한 결과는 아래와 같다.

- **직접 실행** — 정상 사본에서 종료 코드 0, 3개 통과. 컨트롤러 위반 사본에서 규칙 3번 실패 및 종료 코드 2 확인.
- **이벤트 자동 실행** — 임시 프로젝트에서 검토한 Hook을 해당 CLI 실행에 한해 신뢰했다. 간단한 응답을 마칠 때 `Stop`이 ArchUnit 리포트를 새로 생성했고 3개가 통과했다.

## 스터디 소개

| 항목 | 열어볼 파일 | 적용한 내용 | 적용한 이유 |
| --- | --- | --- | --- |
| Markdown | `README.md`, `AGENTS.md`, `docs/architecture.md` | 사용법, 작업 규칙, 의존성 방향을 문서로 기록 | 발표자와 Codex가 작업에 필요한 설명을 찾을 수 있게 한다. |
| Skill | `.agents/skills/change-post-creation/SKILL.md`, `evals/README.md` | 적용 범위, 수정 위치 판단, 테스트 절차, 평가 사례 | 게시글 작성 변경에 필요한 절차를 반복해서 적용한다. |
| ArchUnit | `src/test/java/study/posts/architecture/ArchitectureTest.java` | 운영 클래스의 패키지 의존성 4개 검사 | API 동작이 정상이어도 패키지 경계 위반을 발견한다. |
| Hook | `.codex/hooks.json`, `scripts/check-architecture.ps1` | 응답 종료 전 ArchUnit 실행 | Codex가 작업을 마치기 전에 설계 위반을 확인한다. |

Skill 평가는 네 건을 실행했다. 게시글 작성 변경 세 건에서 Skill을 읽고 구현했으며, 별도 검증에서 모두 통과했다. README 대조군은 README만 수정했고 Skill 읽기 기록이 없었다. [평가 결과](evals/README.md)
