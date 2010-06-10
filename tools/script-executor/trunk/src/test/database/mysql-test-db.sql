CREATE USER 'script'@'localhost' IDENTIFIED BY 'executor';
CREATE DATABASE script_executor;
GRANT ALL PRIVILEGES ON script_executor.* TO 'script'@'localhost';
create table foo (
  id                 int,
  bar                varchar(50) not null,

  primary key (id)
);
insert into foo(id, bar) values(1, 'baz');