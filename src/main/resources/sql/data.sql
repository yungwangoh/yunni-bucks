set global innodb_ft_enable_stopword = OFF;

REPAIR TABLE menu_review QUICK;

set global max_connections = 150;
SHOW VARIABLES LIKE '%max_connection%';
SHOW STATUS LIKE 'Threads_connected';
SHOW VARIABLES LIKE 'innodb_undo%';
show status like '%innodb_undo_logs%';
select count from information_schema.INNODB_METRICS where SUBSYSTEM='transaction' and NAME='trx_rseg_history_len';
show status like 'Com_update%';
show processlist ;

explain analyze select * from menu_review where comments like '%법률에%' limit 100;
SHOW GLOBAL VARIABLES LIKE 'ngram_token_size';
SELECT * FROM INFORMATION_SCHEMA.INNODB_FT_INDEX_CACHE;