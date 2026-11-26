# post-list Specification

## Purpose

클라이언트가 등록된 게시글을 조회하고 일관된 순서로 표시할 수 있도록 목록 응답의 필드와 정렬 기준을 정의한다.

## Requirements

### Requirement: 게시글 목록 조회

시스템은 `GET /posts`에 HTTP 200과 래퍼 없는 JSON 배열을 반환해야 한다(SHALL). 배열의 각 항목은 작성 응답과 같은 공개 필드인 `id`, `title`, `content`만 포함하며 ID 오름차순으로 반환해야 한다(MUST).

#### Scenario: 저장된 글이 없음

- **WHEN** 게시글이 없는 상태에서 `GET /posts`를 호출한다.
- **THEN** HTTP 200과 빈 JSON 배열 `[]`를 반환한다.

#### Scenario: 여러 글을 ID 오름차순으로 반환함

- **WHEN** 여러 게시글이 저장된 상태에서 `GET /posts`를 호출한다.
- **THEN** HTTP 200과 모든 게시글의 `id`, `title`, `content`만 포함한 JSON 배열을 ID 오름차순으로 반환한다.
