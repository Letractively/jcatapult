create table c3_t1 (
  id int auto_increment,
  name varchar(200),
  c2_t1_id int,
  c2_t3_p1_1_id int,
  primary key (id),
  foreign key (c2_t1_id) references c2_t1 (id),
  foreign key (c2_t3_p1_1_id) references c2_t3_p1_1 (id)
) engine=InnoDB default charset=utf8;