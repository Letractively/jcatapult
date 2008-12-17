-- basic seeds into module1_table1
insert into c1_t1 (id, name) values (1, '1.0-seed.sql-value1');
insert into c1_t1 (id, name) values (2, '1.0-seed.sql-value2');

-- this is a seed into a 1.0 alter table.  This insures that the 1.0 alter script
-- ran before this script
insert into c1_t2_p1_0 (id, c1_t1_id) values (1, 1);