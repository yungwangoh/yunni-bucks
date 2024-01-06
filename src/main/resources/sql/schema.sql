drop table if exists menu_review;

create table menu_review
(
    id        bigint auto_increment
        primary key,
    comments  text     null,
    create_at datetime null,
    update_at datetime null,
    member_id bigint   null,
    menu_id   bigint   null,
    fulltext index fx_comments(comments) with parser ngram,
    constraint FKbq51s0styx4qrm18o70ghcwnj
        foreign key (menu_id) references menu (id),
    constraint FKhshcbhsbpub1u25qpe3clhe6g
        foreign key (member_id) references member (id)
)engine=innoDB;