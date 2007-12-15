create table p1_t1 (
  id int auto_increment,
  name varchar(200) not null,
  c1_t1_id int,
  primary key (id),
  foreign key (c1_t1_id) references c1_t1 (id)
) engine=InnoDB default charset=utf8;