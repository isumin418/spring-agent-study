# Codex 작업 규칙

- 작업 전에 관련 코드와 테스트를 확인하고 변경 범위를 요청에 맞춘다.
- 패키지 배치, 계층 간 의존성, 게시글 작성 흐름을 변경하기 전에 [아키텍처 문서](docs/architecture.md)를 읽고 경계를 지킨다.
- 운영 Java 코드를 변경하면 Gradle Wrapper로 전체 테스트를 실행한다.

## 기본 개발 명령

프로젝트 루트에서 실행한다. JDK와 빌드 출력 설정은 [README](README.md)를 참고한다.

| 작업 | Windows PowerShell | macOS·Linux |
| --- | --- | --- |
| 서버 실행 | `.\gradlew.bat bootRun` | `sh ./gradlew bootRun` |
| 전체 테스트 | `.\gradlew.bat test` | `sh ./gradlew test` |
| ArchUnit만 실행 | `.\gradlew.bat test --tests '*ArchitectureTest'` | `sh ./gradlew test --tests '*ArchitectureTest'` |
