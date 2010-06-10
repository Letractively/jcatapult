CREATE USER script WITH PASSWORD 'executor';
CREATE DATABASE script_executor WITH OWNER script;
GRANT ALL PRIVILEGES ON DATABASE script_executor TO script;
create table foo (
  id                 serial,
  bar                varchar(50) not null,

  primary key (id)
);
insert into foo(id, bar) values(1, 'baz');