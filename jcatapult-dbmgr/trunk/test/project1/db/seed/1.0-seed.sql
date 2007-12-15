insert into c1_t1 (id, name) values (100, '1.0-seed.sql-value1');

insert into p1_t1 (id, name, c1_t1_id) values (1000, '1.0-seed.sql-value1', 100);
insert into p1_t1 (id, name, c1_t1_id) values (2000, '1.0-seed.sql-value2', 100);