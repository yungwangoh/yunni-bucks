insert into cart(id, member_id)
values (1, 1);

insert into "order"(id, create_at, order_name, total_price, pay_status, status, update_at, cart_id)
values (1, now(), '카페라떼 외 3건', 9999, 'YES', 'ORDER', null, 1);

insert into card_payment (card_payment_id, approved_at, requested_at, cancel_payment_at, cancel_reason, card_expiration_month, card_expiration_year,
                          card_number, card_password, customer_name, order_uuid, payment_key, payment_status, payment_type, order_id)
values (1, now() - 1, now() + 1, null, null, '11', '23', '9446032384143059', '1234', '홍길동', 'a4CWyWY5m89PNh7xJwhk1', '5zJ4xY7m0kODnyRpQWGrN2xqGlNvLrKwv1M9ENjbeoPaZdL6', 'DONE', 'CARD', 1);

insert into card_payment (card_payment_id, approved_at, requested_at, cancel_payment_at, cancel_reason, card_expiration_month, card_expiration_year,
                          card_number, card_password, customer_name, order_uuid, payment_key, payment_status, payment_type, order_id)
values (2, now() - 1, now() + 1, null, null, '11', '23', '9446032384143059', '1234', '홍길동', 'a4CWyWY5m89PNh7xJwhk2', '5zJ4xY7m0kODnyRpQWGrN2xqGlNvLrKwv1M9ENjbeoPaZdL7', 'DONE', 'CARD', 1);

insert into card_payment (card_payment_id, approved_at, requested_at, cancel_payment_at, cancel_reason, card_expiration_month, card_expiration_year,
                          card_number, card_password, customer_name, order_uuid, payment_key, payment_status, payment_type, order_id)
values (3, now() - 1, now() + 1, null, null, '11', '23', '9446032384143059', '1234', '홍길동', 'a4CWyWY5m89PNh7xJwhk3', '5zJ4xY7m0kODnyRpQWGrN2xqGlNvLrKwv1M9ENjbeoPaZdL8', 'DONE', 'CARD', 1);

insert into card_payment (card_payment_id, approved_at, requested_at, cancel_payment_at, cancel_reason, card_expiration_month, card_expiration_year,
                          card_number, card_password, customer_name, order_uuid, payment_key, payment_status, payment_type, order_id)
values (4, now() - 1, now() + 1, null, null, '11', '23', '9446032384143059', '1234', '홍길동', 'a4CWyWY5m89PNh7xJwhk4', '5zJ4xY7m0kODnyRpQWGrN2xqGlNvLrKwv1M9ENjbeoPaZdL9', 'DONE', 'CARD', 1);

insert into card_payment (card_payment_id, approved_at, requested_at, cancel_payment_at, cancel_reason, card_expiration_month, card_expiration_year,
                          card_number, card_password, customer_name, order_uuid, payment_key, payment_status, payment_type, order_id)
values (5, now() - 1, now() + 1, now() + 5, 'NOT_SATISFIED_SERVICE', '11', '23', '9446032384143059', '1234', '홍길동', 'a4CWyWY5m89PNh7xJwhk5', '5zJ4xY7m0kODnyRpQWGrN2xqGlNvLrKwv1M9ENjbeoPaZdL10', 'CANCEL', 'CARD', 1);

insert into card_payment (card_payment_id, approved_at, requested_at, cancel_payment_at, cancel_reason, card_expiration_month, card_expiration_year,
                          card_number, card_password, customer_name, order_uuid, payment_key, payment_status, payment_type, order_id)
values (6, now() - 1, now() + 1, now() + 5, 'DELAY_DELIVERY', '11', '23', '9446032384143059', '1234', '홍길동', 'a4CWyWY5m89PNh7xJwhk6', '5zJ4xY7m0kODnyRpQWGrN2xqGlNvLrKwv1M9ENjbeoPaZdL11', 'CANCEL', 'CARD', 1);