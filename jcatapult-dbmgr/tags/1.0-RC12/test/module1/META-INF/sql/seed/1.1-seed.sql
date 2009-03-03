-- add another record to module1_table1 so that we can reference it as foreign keys in future inserts
insert into c1_t1 (id, name) values (11, '1.1-seed.sql-module1_table1-value1');

-- add this record to the module1_table2_patch1_0 so that we can reference it as a foreign key in future inserts
insert into c1_t2_p1_0 (id, c1_t1_id) values (2, 11);

-- this verifies that the 1.1-alter.sql files is executed before the 1.1-seed.sql file
-- this is a pretty good test for verifying the foreign key chain of all the tables
-- in module1
insert into c1_t3_p1_1 (id, c1_t2_p1_0_id) values (1, 2);