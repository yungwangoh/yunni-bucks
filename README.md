# ☕️YUNNI-BUCKS

세종대학교 컴퓨터 공학과 19학번 2인 개발팀

개발 기간 

- 1차 : *2023-07 ~ 2023-09 (MVP 구현 완료)*
- 2차 : *2023-10 ~ ing (최적화 진행중)*


## 목차
 - [프로젝트 소개](#프로젝트-소개)
 - [맴버 소개](#맴버-소개)
 - [개발 환경](#개발-환경)
 - [프로젝트 설명](#프로젝트-설명)
 - [세부 내용](#세부-내용)


## 프로젝트 소개

---

주문-결제-배달 온라인 카페 서비스

## 맴버 소개

---
- [하윤](https://github.com/gkdbssla97)
- [윤광오](https://github.com/yungwangoh)

## 개발 환경

---

- Java 17
- Oracle OpenJDK 17.0.4
- IDE : IntelliJ IDEA
- DATABASE : H2, MariaDB
- ORM : JPA
- Framework: Spring Boot 2.7.14

## 기술 스택

---
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=spring&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-0095D5?style=flat-square&logo=Java&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2-87B6A7?style=flat-square&logo=h2&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)
![RestDocs](https://img.shields.io/badge/RestDocs-59666C?style=flat-square&logo=Spring&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-4682B4?style=flat-square&logo=java&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-6CB3E6?style=flat-square&logo=OpenAPI-Initiative&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=JSON-Web-Tokens&logoColor=white)
![Logback](https://img.shields.io/badge/Logback-1D1D1D?style=flat-square&logo=logback&logoColor=white)


## 프로젝트 설명

---

### 아키텍처

#### Flow Chart

<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/4a8f2a6d-4508-407b-a511-68374f6c3080">

#### ERD

<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/37452648-0c1b-4b6f-aa0a-42a53cbcc9ce">

#### Domain Model
<img width="757" alt="image" src="https://github.com/yungwangoh/yunni-bucks/assets/37898720/35e060e7-dd72-46c7-90f0-b28fc55b36d7">

### Layered Architecture
DB 모듈을 손쉽게 전환하기 위해 설계

- 주문

<img width="400" alt="image" src="https://github.com/yungwangoh/yunni-bucks/assets/37898720/9de88228-012f-4a70-b63f-593b8d7929eb">

- 회원

<img width="400" alt="image" src="https://github.com/yungwangoh/yunni-bucks/assets/37898720/a3e3b845-f46f-404b-8bba-86b9dbaec4ba">

- 결제

<img width="400" alt="image" src="https://github.com/yungwangoh/yunni-bucks/assets/37898720/d4462d37-83da-451b-94d9-9b32cc45dd43">

## 프로젝트 구조

[프로젝트 디렉토리](https://github.com/gkdbssla97/yunni-bucks/blob/master/DIRECTORY-STRUCTURE.md)

## 세부 내용

- [velog](https://velog.io/@swager253/series/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8)