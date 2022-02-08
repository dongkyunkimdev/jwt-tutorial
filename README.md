# jwt-tutorial
### 1. 목표
Spring Security를 이용하여 JWT 인증과 인가 및 회원가입, 로그인 서비스를 구현하고 단위테스트, API 문서 작성

### 2. 프로젝트 환경
- IDE : IntelliJ
- ORM : Spring Data JPA
- auth : Spring Security, JWT
- DB : H2(in-memory)
- Test : JUnit5, Mokcito

### 3. 디렉토리 구조
    ├── src
    │   ├── main
    |   |   ├── java
    |   |   |   ├── kdk.jwttutorial
    |   |   |   |   ├── error
    |   |   |   |   |   ├── exception
    |   |   |   |   ├── security
    |   |   |   |   |   ├── auth
    |   |   |   |   |   ├── jwt
    |   |   |   |   ├── swagger
    |   |   |   |   ├── user
    |   |   ├── resources
    |   |   |   ├── application.yml
    |   |   |   ├── data.sql
    │   ├── test
    |   |   ├── java
    |   |   |   ├── kdk.jwttutorial
    |   |   |   |   ├── security
    |   |   |   |   ├── user
    ├── build.gradle
    └── README.md
> 도메인형 구조
