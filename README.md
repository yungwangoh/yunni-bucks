# ☕️YUNNI-BUCKS

세종대학교 컴퓨터 공학과 19학번 2인 개발팀

개발 기간 : *2023-07 ~ 2022-09 (MVP 구현 완료)* 

## 목차
 - [프로젝트 소개](#프로젝트-소개)
 - [맴버 구성](#맴버-구성)
 - [개발 환경](#개발-환경)


## 프로젝트 소개
주문-결제-배달 온라인 카페 서비스

## 맴버 구성
|        | **윤광오(팀장)**                                                                                                                                                                                                                                               | 하윤(팀원)                                                                                        |
|--------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
|        |                                                                                                                                                                                                                                                           |                                                                                               |
| 포지션    | Back-End Developer                                                                                                                                                                                                                                        | Back-End Developer                                                                            |
| 담당 도메인 | 회원, 주문, 배달                                                                                                                                                                                                                                                | 결제                                                                                            |
| 기술 스택  | Spring Boot, Redis, Query-Dsl, Spring Data JPA, JPA, JUnit, H2 Database, MariaDB, Rest Docs, Mockito, JWT                                                                                                                                                 | Spring Boot, Query-Dsl, Spring Data JPA, JPA, JUnit, H2 Database, MariaDB, Rest Docs, Mockito |
| 한 일    | 설계 : ERD (DB), Domain Model, OOP, Layered Architecture<br/><br/> 구현: Java Reflection 활용한 Record Class 전용 CustomMapper, Fake Repository, Redis(NoSql) Fake Repository 구현, Scheduler 활용한 배달 상태 변경 구현, 자체 비밀번호 암호화 구현, 썸네일 파일 업/다운로드, JWT 활용한 Login, 페이지네이션, |                                                                                               |

## 개발 환경
- Java 17
- Oracle OpenJDK 17.0.4
- IDE : IntelliJ IDEA
- DATABASE : H2, MariaDB
- ORM : JPA
- Framework: Spring Boot 2.7.14

## 프로젝트 설명


### ERD

<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/aff78566-56fe-42db-83f2-fde016bdbc93">

### Domain Model
<img width="757" alt="image" src="https://github.com/yungwangoh/yunni-bucks/assets/37898720/35e060e7-dd72-46c7-90f0-b28fc55b36d7">

### 1.SRP
주문, 결제, 할인, 배달은 각각의 기능만 가지며 책임을 수행한다.
<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/8f1e577a-843f-461a-a356-d1a4c3c46c49">

### 2.OCP
기존 구성요소는 수정이 일어나지 말아야 하며, 쉽게 확장해서 재사용을 할 수 있어야 하므로 구현보다는 인터페이스에 의존하도록 설계한다.
모듈별 인터페이스를 두어 코드 재사용이 용이하다. Unit Test 소형 테스트 진행에 수월하다.

<img width="359" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/c333f588-7561-4b27-afdd-a453af0d6e74">

### 3.ISP
인터페이스의 단일책임을 강조하여 Service, Repository layer 계층 별 서로 다른 성격의 인터페이스를 명백히 분리한다.

<img width="539" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/706880be-4877-4a8d-a3db-27092e5bbf17">

### 4.DIP
Transitive Dependency가 발생했을 때 상위 레벨의 레이어가 하위 레벨의 레이어를 바로 의존하게 하지 않고 둘 사이에 존재하는 추상레벨을 통해 의존한다. 상위 레벨의 모듈은 하위 레벨의 모듈의 의존성을 벗어나 재사용 및 확장성을 보장받는다.

<img width="538" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/d7296b2a-496d-4487-b532-114976ecec9b">


### Integration Test / Unit Test -> Fake Object, Mock를 활용 (진행 중)

**Payment Test Result**

- **Repository**
1. JpaRepository
   
    <img width="339" alt="Untitled" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/96a8506c-5b2c-4077-86f7-ac6b989b911d">

    
2. FakeRepository
    
    <img width="339" alt="Untitled" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/cef19145-01d0-4ba2-9e95-fb54b37c97da">

Fake Object Repository 구현, UnitTest 시 통합 테스트 속도 대비 약 *8배* 단축

- **Service**
