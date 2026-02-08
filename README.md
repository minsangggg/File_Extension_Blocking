# File Extension Blocking

파일 확장자 차단 정책을 관리하는 과제 구현입니다.

## 기술 스택
- Java 17 / Spring Boot 3
- PostgreSQL
- 단일 HTML + JavaScript(fetch)

## 로컬 실행 방법
1) PostgreSQL 실행
- DB: `fileblock`
- USER/PASS: `fileblock` / `fileblock`

2) 애플리케이션 실행
```bash
mvn spring-boot:run
```

3) 접속
- `http://localhost:8080`

## Docker Compose 실행 방법
1) 로컬에서 JAR 빌드
```bash
mvn -DskipTests package
```

2) Docker 실행
```bash
docker compose up -d --build
```
- 접속: `http://localhost:8080`

## EC2 배포 절차
1) EC2 보안그룹 인바운드 오픈
- 22 (SSH)
- 8080 (서비스 포트)

2) 서버 접속 후 실행
```bash
git clone <your_repo_url>
cd file-extension-blocking
mvn -DskipTests package
docker compose up -d --build
```

3) 접속 URL
- `http://<EC2_PUBLIC_IP>:8080`

4) Elastic IP 사용 권장
- 면접일까지 고정 URL 유지를 위해 Elastic IP 할당 권장

## 테스트 시나리오
- 고정 확장자 체크/해제 후 새로고침 시 상태 유지
- 커스텀 확장자 추가 후 태그 표시
- 커스텀 확장자 중복 추가 시 409 응답
- 커스텀 확장자 200개 초과 시 409 응답
- 커스텀 확장자 삭제 시 태그 제거 + DB 삭제
- 파일 업로드 시 차단 확장자는 409 응답, 허용 확장자는 업로드 가능 메시지 표시

## 추가로 고려한 점
- 입력 정규화: trim, 앞의 점 제거, 소문자 통일
- 커스텀 중복 방지: ext UNIQUE + 409 응답
- 200개 제한 서버 강제
- 고정 목록에 존재하는 확장자를 커스텀으로 추가 시 409 거부
- 없는 리소스 수정/삭제는 404

## API 명세
- `GET  /api/extensions/fixed`
- `PUT  /api/extensions/fixed/{ext}`
  - body: `{ "blocked": true/false }`

- `GET  /api/extensions/custom`
- `POST /api/extensions/custom`
  - body: `{ "ext": "sh" }`
- `DELETE /api/extensions/custom/{id}`

- `POST /api/files/upload`
  - multipart/form-data `file`
