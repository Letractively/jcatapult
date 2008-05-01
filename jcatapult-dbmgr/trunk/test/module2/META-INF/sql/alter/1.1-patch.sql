create table c2_t3_p1_1 (
  id int auto_increment,
  c1_t3_p1_1_id int,
  c2_t2_p1_0_id int,
  primary key (id),
  foreign key (c1_t3_p1_1_id) references c1_t3_p1_1 (id),
  foreign key (c2_t2_p1_0_id) references c2_t2_p1_0 (id)
) engine=InnoDB default charset=utf8;