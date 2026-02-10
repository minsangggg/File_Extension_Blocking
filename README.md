# File Extension Blocking

파일 확장자 차단 정책을 관리하는 과제입니다.

## 기술 스택
- Java 17 / Spring Boot 3
- PostgreSQL
- 단일 HTML + JavaScript(fetch)

## 요구사항 체크리스트
- [o] 고정 확장자 체크박스 표시 (bat, cmd, com, cpl, exe, scr, js)
- [o] 고정 확장자 기본 상태 unchecked
- [o] 고정 확장자 체크/해제 시 DB 저장 및 새로고침 유지
- [o] 고정 확장자는 커스텀 태그 영역에 표시하지 않음
- [o] 커스텀 확장자 입력 최대 20자
- [o] 커스텀 추가 시 DB 저장 + 태그 표시
- [o] 커스텀 최대 200개 제한
- [o] 커스텀 삭제(X) 시 DB 삭제
- [o] 파일 업로드 시 차단 확장자 거부 메시지 표시

## 추가로 고려한 점
- 입력 정규화: trim, 앞의 점 제거, 소문자 통일
- 허용 문자 제한: 영문 소문자/숫자만 허용
- 확장자 길이 제한: 20자 이내
- 커스텀 중복 방지: 동일 확장자 재등록 불가
- 커스텀 200개 제한: 최대 200개
- 고정/커스텀 중복 정책: 고정 목록은 커스텀 추가 불가
- 고정 확장자 시드: 설정(app.fixed-extensions) 기반으로 누락 시 자동 생성
- 없는 리소스 처리: 없는 항목은 변경 불가
- 파일 업로드 검증: 파일명에서 확장자 추출, 확장자 없으면 거부
- 업로드 차단 정책: 고정 체크/커스텀 등록 확장자는 차단
- 업로드 저장 범위: 저장하지 않고 검증/차단만 수행

## API 명세(요약)
- `GET  /api/extensions/fixed`
- `PUT  /api/extensions/fixed/{ext}` body: `{ "blocked": true/false }`
- `GET  /api/extensions/custom`
- `POST /api/extensions/custom` body: `{ "ext": "sh" }`
- `DELETE /api/extensions/custom/{id}`
- `POST /api/files/upload` multipart/form-data `file`

## 배포(EC2 + Elastic IP)
- 배포 주소: `http://15.165.177.33:8080/` (Elastic IP)
- 인스턴스: Ubuntu 22.04 LTS / t2.micro
- 보안 그룹: SSH(22), HTTP(80), TCP(8080) 허용

