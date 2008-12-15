alter table users add column partial bit;
update users set partial = false;