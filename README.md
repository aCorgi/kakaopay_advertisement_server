# 카카오페이 광고 서비스_매일 모으기 서버 과제
## 지원자 정보
서버개발 지원자 이해원


## Local 환경 테스트 (Docker 필요)

*docker compose 명령어*

```
docker compose up -d
```


## tech stack
-   kotlin : 1.9.25
-   java : 21
-   spring boot : 3.5.3
-   gradle : 8.5
-   docker-compose
    - mysql:8.0.42
    - redis:7
    - rabbitmq:3-management

### test
-   mockito
-   junit 5
- mockwebserver
- okhttp
- testcontainer
- awaitility

### 그 외 라이브러리

- spring-security
- jpa
- queryDSL
- spring-validation
- ktlint
- redisson
- springdoc-openapi-starter-webmvc-ui : swagger ui 를 사용하기 위함


## 디렉토리 구조

**spring MVC**

```
autoever-security
├─ README.md
├─ docker-compose.yml
├─ ...
└─ src
   ├─ main
   │  ├─ kotlin
   │  │  └─ com
   │  │     └─ task
   │  │        └─ kakaopayadvertisementserver
   │  │           ├─ aspect
   │  │           ├─ client
   │  │           ├─ component
   │  │           ├─ config
   │  │           ├─ controller
   │  │           │  ├─ admin
   │  │           │  ├─ user
   │  │           ├─ converter
   │  │           ├─ domain
   │  │           │  ├─ embeddable
   │  │           │  └─ entity
   │  │           ├─ dto
   │  │           │  ├─ api
   │  │           │  ├─ embeddable
   │  │           │  ├─ event
   │  │           │  └─ message
   │  │           ├─ exception
   │  │           ├─ property
   │  │           ├─ repository
   │  │           ├─ service
   │  │           └─ util
   │  └─ resources
   │     ├─ application-prod.yml
   │     ├─ application.yml
   │     └─ logback-spring.xml
   └─ test
      ├─ kotlin
      │  └─ com
      │     └─ task
      │        └─ kakaopayadvertisementserver
      │           ├─ component
      │           ├─ config
      │           ├─ controller
      │           ├─ repository
      │           ├─ service
      │           └─ util
      └─ resources
         └─ application.yml

```

#### 1 depth directory

-   client : 외부 api client 의 로직이 담겨 있음
-   component: mq, redis 같은 biz or applicationEventHandler
-   config : configuration 등 설정 관련 bean
-   controller : 컨트롤러 관련
-   converter : db <-> entity converter
-   domain : entity 에 활용하는 클래스
-   dto : api req & res 또는 event, message 등 내부에서 횔용하는 객체를 정의
-   exception : 핸들링을 하거나 커스터마이징한 exception 클래스
-   property : application 설정이 담긴 bean
-   repository : DB layer
-   service : 서비스 관련
-   util : 확장 함수, constant 등 유틸성


## 시스템 아키텍쳐



## 설계 핵심 포인트
- **Basic Auth 방식으로 Spring security 구현했습니다.**
    - Admin 과 User 두 개의 GrantedAuthority 가 존재합니다.
        - `/admin/**` URL 의 API 는 Admin Authority 가 필요합니다.
        - `/user/**` URL 의 API 는 User Authority 가 필요합니다.
    - 통상적으로는 User 는 JWT 를 사용하지만, 여기서는 Admin api 가 섞여 있어 Basic Auth 한 가지로만 통일했습니다.
        - DB Table 또한 별도 분리하는 게 맞으나, 가시성을 위해 하나의 테이블에 admin, user 데이터가 공존합니다.
        - authorities 라는 json column 으로 계정 별로 가질 수 있는 Authority 를 분류합니다.
    - Basic Auth token 검증을 위해 DB 에 접근합니다.
        - username = email, password = password 로 DB 에 존재하면 Authenticate 합니다.
        - DB의 password 는 BCrypt 알고리즘으로 암호화해 저장합니다.
            - 단방향 암호화 알고리즘이라, 탈취되더라도 복호화가 불가능합니다.
- **AuthenticationPrincipal 로 User api 호출 시 token 에 담긴 memberId 를 사용합니다.**
    - UserDetails 을 wrapping 한 클래스를 선언하여 memberId 를 담습니다.
    - spring security filter 를 통과했기에, 해당 토큰은 User 권한을 가지고 있습니다. <br>
    토큰에 담긴 memberId 는 유저의 ID 로 활용할 수 있습니다.
- **DB 테이블에 통용되는 개념에 맞게 unique index 와 join 을 활용했습니다.**
    - 한 명의 사용자는 하나의 광고에 중복 참여할 수 없습니다.
        - advertisement_id 와 member_id 를 unique index 로 설정하고, 비즈니스 로직에서 중복 체크합니다.
    - 광고 특성 상 N 개의 광고 참여 자격이 존재합니다.
        - 광고와 광고 참여 자격 테이블의 관계는 1:N 입니다.
    - 광고 참여 일시 = 광고 참여 이력 생성 일시
    - 광고 참여 자격이 추가되면, enum 및 테이블에 데이터 INSERT 로 유연하게 대응합니다.
- **대규모 트래픽에 유연하게 대응합니다.**
    - 광고 참여 시 포인트 적립 기능은 분산 처리합니다.
        - 사전에 광고 참여 여부 검증을 진행합니다.
        - 포인트 적립은 외부 API 를 호출합니다.
        - 포인트 적립을 성공할 때까지 사용자가 blocking 되지 않게 합니다.
    - 다음과 같은 프로세스로 동작합니다.
        - 사용자가 해당 광고에 참여할 수 있으면 우선 DB 데이터 상 참여 이력을 쌓습니다.
        - DB transactional Commit 성공 시 ApplicationEventPublisher 로 포인트 적립을 위한 이벤트를 발행합니다. <br>
        (TransactionalEventListener)
        - @Async 로 동작하며, 이벤트 로깅을 위해 RabbitMQ 에 메세지를 발행합니다.
        - RabbitMQ 에서 메세지를 consume 하여 포인트 적립 API 를 호출합니다.
        - RestClient API 호출 시 가상 쓰레드를 사용합니다.
    - 포인트 적립에 실패하더라도 requeue 할 수 있어 Retry 가 가능하고, 광고 참여 이력 데이터를 가지고 적립 여부를 검증할 수 있습니다.
- **동시성을 제어합니다.**
    - redisson 을 활용해 광고 참여 전 광고ID 단위로 분산 락을 설정합니다.
        - lock 을 얻지 못하면 일정 기간동안 retry 하며 대기합니다.
        - 결국 lock 을 얻지 못하면 400 오류를 반환합니다.
    - 광고 데이터의 참여 횟수 counting 이후 lock 을 반환합니다.
        - 도중 오류가 발생하더라도, TTL 이 설정되어 있어 race condition 이 발생하지 않습니다.


## API 명세서
서버를 띄우면 swagger UI 에 접근 가능합니다.
http://localhost:8080/v1/swagger-ui/index.html


## API 로직 설명
1. **(어드민) 광고 등록 API**
    1. 광고를 시스템에 등록하기에, 어드민 권한만 등록 가능합니다.
    2. 광고 정보와 해당 광고에 주입할 광고 참여 자격 정보들을 함께 요청받아 DB 에 저장합니다.
    3. 광고명 중복 체크를 진행합니다.


2. **(유저) 해당 유저에게 노출되는 참여 가능 광고 목록 조회 API**
    1. memberId 에 해당하는 회원 정보가 존재하는 지 검증합니다.
    2. QueryDSL 을 활용해 다음과 같은 광고들만 조회합니다.
        1.  조회한 현재 시점 기준으로 노출 기간에 BETWEEN 합니다.
        2.  최대 광고 참여 횟수보다 현재 광고 참여 횟수가 작아야 합니다.
        3.  보상 금액이 가장 큰 순서대로 정렬합니다.
    3. 해당 광고의 참여 자격이 존재한다면, 참여 자격 여부를 확인합니다.
    4. 참여 가능한 광고만 최대 10개까지 list 로 반환합니다.

3. **(유저) 광고 참여 API**
    1. memberId 에 해당하는 회원 정보가 존재하는 지 검증합니다.
    2. 광고 정보가 존재하는 지 검증합니다.
    3. 이미 해당 광고에 참여했는 지 검증합니다.
    4. 해당 광고 참여 자격에 부합한 지 검증합니다.
    5. lock 을 획득합니다.
        - lock 을 획득하지 못하면 400 오류를 반환합니다.
    6. 광고 참여 이력을 저장합니다.
    7. 광고 참여 횟수를 1 증가합니다.
    8. 광고 참여에 성공했다는 ApplicationEvent 를 발행합니다.
    9. DB commit 시 발행한 이벤트를 실행시킵니다.
        - message Queue 에 포인트 적립을 위한 정보를 담은 메세지를 발행합니다.
    10. message Queue 를 consume 합니다.
        - 포인트 적립을 위한 외부 API 를 호출합니다.

4. **(유저) 광고 참여 이력 페이징 조회 API**
    1. 조회하는 시작 일시와 종료 일시를 검증합니다.
    2. 참여 이력 생성 일시 (=참여 일시) 를 오래된 순으로 정렬하여, 페이징 조회합니다.

