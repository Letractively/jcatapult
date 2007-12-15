create table c1_t2_p1_0 (
  id int auto_increment,
  c1_t1_id int,
  primary key (id),
  foreign key (c1_t1_id) references c1_t1 (id)
) engine=InnoDB default charset=utf8;