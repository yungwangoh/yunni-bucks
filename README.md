# ☕️YUNNI-BUCKS

세종대학교 컴퓨터 공학과 19학번 2인 개발팀

개발 기간 

- 1차 : *2023-07 ~ 2023-09 (MVP 구현 완료)*
- 2차 : *2023-10 ~ ing (최적화 진행중)*

## 아키텍처

### 백엔드 아키텍처
![002](https://github.com/yungwangoh/yunni-bucks/assets/37898720/fa56489d-a9f7-442a-a803-c7c6c22b87b7)

### ERD
<img width="757" alt="image" src="https://github.com/gkdbssla97/yunni-bucks/assets/55674664/37452648-0c1b-4b6f-aa0a-42a53cbcc9ce">

## 개선점

- [Fake Test를 통해 외부 테스트 라이브러리 없이 테스트하여 단위 테스트 격리](https://velog.io/@swager253/Yunny-Bucks.-Test-Fake-Repository)
- [Lock을 통해 동시성 이슈 해결 (Optimistic Lock vs Pessimistic Lock)](https://velog.io/@swager253/Yuuny-Bucks.-JPA-%EC%97%90%EC%84%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88)
- [Redis Cache를 활용하여 조회 성능 개선](https://velog.io/@swager253/Yunny-Bucks.-%EC%A1%B0%ED%9A%8C-%EC%84%B1%EB%8A%A5-%EC%B5%9C%EC%A0%81%ED%99%94)
- [회원 등급 업데이트 기능 성능 개선. 단건 업데이트에서 일괄 업데이트로 DB 성능 개선](https://velog.io/@swager253/Yunny-Bucks.-%ED%9A%8C%EC%9B%90-%EB%93%B1%EA%B8%89-%EC%97%85%EB%8D%B0%EC%9D%B4%ED%8A%B8-%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0-Batch-update)
- [MySQL Full-Text-Search 기능을 활용하여, 전문 검색 (Full-Text-Search) 성능 개선](https://velog.io/@swager253/Yunny-bucks.-Full-Text-Search-%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0)
- K6, Grafana로 부하 테스트를 진행하여, 상품 재고 증감 기능 DB Lock 병목 성능 개선
- Mysql DB Scale-out 하여 Write/Read Replication
