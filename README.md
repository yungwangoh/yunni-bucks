# ☕️YUNNI-BUCKS

세종대학교 컴퓨터 공학과 19학번 2인 개발팀

개발 기간 : *2023-07 ~ 2022-09 (MVP 구현 완료)* 

## 목차
 - [프로젝트 소개](#프로젝트-소개)
 - [맴버 구성](#맴버-구성)
 - [개발 환경](#개발-환경)
 - [프로젝트 설명](#프로젝트-설명)


## 프로젝트 소개
주문-결제-배달 온라인 카페 서비스

## 맴버 구성
|                    | **윤광오(팀장)**                                                                                                                                                                                                                                                                       | 하윤(팀원)                                                                                                                                                                                                                                                                                                                                          |
|:-------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|                    |                                                                                                                                                                                                                                                                                   |                                                                                                                                                                                                                                                                                                                                                 |
| 포지션                | Back-End Developer                                                                                                                                                                                                                                                                | Back-End Developer                                                                                                                                                                                                                                                                                                                              |
| 담당 도메인             | 회원, 주문, 배달                                                                                                                                                                                                                                                                        | 결제, 카드, OCR(Optical Character Recognition)                                                                                                                                                                                                                                                                                                      |
| 기술 스택              | Spring Boot, Redis, Query-Dsl, Spring Data JPA, JPA, JUnit, H2 Database, MariaDB, Rest Docs, Mockito, JWT                                                                                                                                                                         | Spring Boot, Query-Dsl, Spring Data JPA, JPA, JUnit, H2 Database, MariaDB, Rest Docs, Mockito, Open API                                                                                                                                                                                                                                         |
| 한 일                | 설계 : ERD (DB), Domain Model, OOP, Layered Architecture<br/><br/> 구현: Java Reflection 활용한 Record Class 전용 CustomMapper, Fake Repository, Redis(NoSql) Fake Repository 구현, Scheduler 활용한 배달 상태 변경 구현, 자체 비밀번호 암호화 구현, 썸네일 파일 업/다운로드, JWT 활용한 Login, 페이지네이션, 디스코드 웹훅을 활용한 에러 로그 모니터링 | 설계 : OOP, Layered Architecture(Pay, Card, OCR)<br/><br/> 구현: Clova OCR(https://clova.ai/ocr/), Toss Payments(https://docs.tosspayments.com/guides/index) develop API 연동하여 신용/체크카드 이미지 인식 및 자동 결제 시스템 개발</br>Fake Object, Test Container로 Fake Layer Architecture 구현 Unit/Integration Test, Pagination, Slack Error Log, API 요청 알림 비동기 멀티 쓰레드 구현 |

## 개발 환경
- Java 17
- Oracle OpenJDK 17.0.4
- IDE : IntelliJ IDEA
- DATABASE : H2, MariaDB
- ORM : JPA
- Framework: Spring Boot 2.7.14

## 프로젝트 설명


### Architecture

<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/4881409d-6661-4591-91f0-896ed3c48029">

### Flow Chart

<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/4a8f2a6d-4508-407b-a511-68374f6c3080">

### ERD

<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/37452648-0c1b-4b6f-aa0a-42a53cbcc9ce">

### Domain Model
<img width="757" alt="image" src="https://github.com/yungwangoh/yunni-bucks/assets/37898720/35e060e7-dd72-46c7-90f0-b28fc55b36d7">

### 프로젝트 구조

[프로젝트 디렉토리](https://github.com/gkdbssla97/yunni-bucks/blob/master/DIRECTORY-STRUCTURE.md)

### 1.SRP
주문, 결제, 할인, 배달은 각각의 기능만 가지며 책임을 수행한다.
<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/8f1e577a-843f-461a-a356-d1a4c3c46c49">

### 2.OCP
기존 구성요소는 수정이 일어나지 말아야 하며, 쉽게 확장해서 재사용을 할 수 있어야 하므로 구현보다는 인터페이스에 의존하도록 설계한다.
모듈별 인터페이스를 두어 코드 재사용이 용이하다. Unit Test 소형 테스트 진행에 수월하다.

<img width="359" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/c333f588-7561-4b27-afdd-a453af0d6e74">
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
<img width="359" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/dd6c7f4a-172a-4a97-b2b2-e3811f117073">

### 3.ISP
인터페이스의 단일책임을 강조하여 Service, Repository layer 계층 별 서로 다른 성격의 인터페이스를 명백히 분리한다.

<img width="539" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/706880be-4877-4a8d-a3db-27092e5bbf17">

### 4.DIP
Transitive Dependency가 발생했을 때 상위 레벨의 레이어가 하위 레벨의 레이어를 바로 의존하게 하지 않고 둘 사이에 존재하는 추상레벨을 통해 의존한다. 상위 레벨의 모듈은 하위 레벨의 모듈의 의존성을 벗어나 재사용 및 확장성을 보장받는다.

<img width="538" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/d7296b2a-496d-4487-b532-114976ecec9b">


### Integration Test / Unit Test 속도 비교 

*PaymentTest Result (Fake Object, TestContainer, MockMvc 사용)*

Integration Test 대비 Unit Test 속도 약 *8배* 단축 (Test DB를 활용한 테스트보다 Fake Repository 를 활용한 테스트가 월등히 빠르다.)
- **Repository**   

<img width="339" alt="Untitled" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/96a8506c-5b2c-4077-86f7-ac6b989b911d">
&nbsp;
<img width="329" alt="Untitled" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/cef19145-01d0-4ba2-9e95-fb54b37c97da">

- **Service**
<img width="364" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/2e571fdd-ad3b-40a1-8fed-bdf9618ec84c">
&nbsp;
<img width="394" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/fc5763b7-2a8b-4148-a577-38dbabb529db">

- **Controller**
<img width="364" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/8536f169-d312-4fc9-a8c2-bd45ce77c942">
&nbsp;
<img width="374" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/e8132222-dc1b-4c19-b570-8cc00d0e5471">

- **Mock**

그리고 Mock 이용한 테스트 보다 100 ~ 200ms 의 속도가 더 빠르다.

Mock을 이용한 테스트는 각 실제 코드의 캡슐화를 위반하기 때문에 변경에 대해 취약하다. 이는 Fake를 활용함으로써 변경에 대해 둔감하도록 하였다.

왼쪽이 Mocking한 test, 오른쪽이 Fake Test이다.


<img width="350" alt="스크린샷 2023-09-15 오후 2 54 06" src="https://github.com/yungwangoh/yunni-bucks/assets/37898720/16b5b49b-264a-4489-ac7c-73478a331a8b">
&nbsp;
<img width="350" alt="스크린샷 2023-09-15 오후 2 54 34" src="https://github.com/yungwangoh/yunni-bucks/assets/37898720/0731c0f5-61ea-40fb-8ddf-390cfe575ef4">
