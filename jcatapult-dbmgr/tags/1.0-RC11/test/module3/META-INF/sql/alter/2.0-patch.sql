create table c3_t2_p2_0 (
  id int auto_increment,
  c2_t3_p1_1_id int,
  primary key (id),
  foreign key (c2_t3_p1_1_id) references c2_t3_p1_1 (id)
) engine=InnoDB default charset=utf8;