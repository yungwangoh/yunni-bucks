alter table menu_review add fulltext index fx_comment(comments) with parser ngram;