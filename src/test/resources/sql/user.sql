set foreign_key_checks = 0;
truncate table member;
set foreign_key_checks = 1;
insert into member (create_at, update_at, city, detail, district, zip_code, coupon_id, email, total_price, user_name, order_count, password, user_rank, ID, version)
values (now(), now(), '서울시', '능동로 100 세종대학교', '광진구', '100-100', null, 'qwer1234@naver.com', 0, '홍길동', 0, '010d1f38a09a86af740da15adbfb1da5869aa6a288d25f3bf08b7d13cdea04fc', 'BRONZE', 1, 0);