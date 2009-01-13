create table c3_t3_p2_1 (
  id int auto_increment,
  c3_t2_p2_0_id int,
  primary key (id),
  foreign key (c3_t2_p2_0_id) references c3_t2_p2_0 (id)
) engine=InnoDB default charset=utf8;