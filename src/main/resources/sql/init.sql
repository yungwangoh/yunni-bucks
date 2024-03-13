set foreign_key_checks = 0 ;
truncate table `order`;
truncate table member;
truncate table cart;
truncate table cart_item;
truncate table menu;
set foreign_key_checks = 1;
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 1, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '커피빵', now(), 100000000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
VALUES ( 'Beverage', 2, now(), '에티오피타 커피산', 'M', 80, 80, 80, 80, 1000, '커피', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 3, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '크림빵', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 4, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '소금빵', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 5, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '카레빵', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 6, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '호빵', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 7, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '꽈배기', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 8, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '소다빵', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 9, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '소라빵', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 10, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '소보로빵', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 11, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '프레즐', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 12, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '스콘', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 13, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '베이글', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 14, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '바게트', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 15, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '미트파이', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 16, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '초코 소라빵', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 17, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '휘낭시에', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 18, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '도넛', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 19, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '크림 베이글', now(), 10000, 0, 0);
insert into menu (dtype, id, create_at, description, menu_size, carbohydrates, fats, kcal, proteins, total_price, title, update_at, QUANTITY, ORDER_COUNT, version)
values ( 'Bread', 20, now(), '에티오피아 커피산', 'M', 80, 80, 80, 80, 1000, '초코 크림 베이글', now(), 10000, 0, 0);
insert into member (create_at, update_at, city, detail, district, zip_code, coupon_id, email, total_price, user_name, order_count, password, user_rank, ID, version)
values (now(), now(), '서울시', '능동로 100 세종대학교', '광진구', '100-100', null, 'qwer1234@naver.com', 0, '홍길동', 0, '010d1f38a09a86af740da15adbfb1da5869aa6a288d25f3bf08b7d13cdea04fc', 'BRONZE', 1, 0);
insert into cart (id, version, member_id) VALUES (1, 0, 1);
insert into cart_item (id, cart_id, menu_id) VALUES (1, 1, 1);