# 팀 Todo 실시간 알림 시스템

## 📌 프로젝트 개요
팀 단위 Todo 관리 웹 서비스에 실시간 알림 기능을 도입하기 위한 백엔드 개발 프로젝트입니다.  
사용자 작업 및 상태 변화에 대해 즉각적인 알림을 제공하여 효율적인 협업 환경을 지원합니다.

## 🛠 기술 스택
- Java 17
- Spring Boot 3.x
- Spring Security
- MariaDB
- JPA (Hibernate)
- Redis (Pub/Sub)
- Server-Sent Events (SSE)
- Git & GitHub

## 📂 프로젝트 구조 (예정)
- config: 시스템 설정(Redis, Security, SSE 등)
- controller: API 엔드포인트 및 SSE 구독 관리
- service: 알림 및 Todo 관련 비즈니스 로직
- repository: DB 연동 (Todo, 알림 등)
- listener: Redis 메시지 구독 처리
- dto: 데이터 전송 객체
- utils: 메시지 포맷, Emitter 저장소 관리

## ✅ 현재까지 진행 상황
- 프로젝트 초기 세팅 완료 (GitHub 원격 저장소 연결, 기본 Gradle 빌드 구성)
- MariaDB 연동 테스트 및 데이터베이스 생성 준비 완료
- Redis 설치 및 기본 Pub/Sub 기능 테스트 중

## 🚧 앞으로 할 일
- Todo 기본 CRUD API 구현
- SSE 기반 실시간 알림 기능 개발 시작
- Redis Pub/Sub 메시지 발행/구독 기능 완성
- 사용자 인증 및 권한 관리 적용 (Spring Security)
- 클라이언트와의 SSE 연결 안정성 확보 (재연결, 타임아웃 처리)
- 알림 데이터 DB 저장 및 읽음 상태 관리 구현

---

## 💬 커밋 메시지 컨벤션

모든 커밋 메시지는 아래 형식을 따릅니다:

### 예시
- (feat): 팀 생성 기능 구현  
- (fix): 알림이 중복 전송되던 문제 해결  
- (refactor): UserService 리팩토링  
- (style): 코드 포맷 정리  
- (docs): README 수정  
- (test): TodoService 단위 테스트 추가  
- (chore): 의존성 및 설정 파일 수정  
- (update): 메시지 포맷 상수 변경

### 타입 설명
| 타입       | 설명                                |
|------------|-------------------------------------|
| feat       | 새로운 기능 추가                     |
| fix        | 버그 수정                           |
| refactor   | 코드 리팩토링 (기능 변화 없음)       |
| style      | 코드 스타일 변경 (포맷팅, 세미콜론 등)|
| docs       | 문서 수정 (README 등)                |
| test       | 테스트 코드 추가/수정                |
| chore      | 설정, 빌드 관련 잡일                 |
| update     | 기존 코드나 설정의 간단한 업데이트   |



---

본 README는 프로젝트 진행 상황에 따라 지속 업데이트할 예정입니다.
