create table c2_t2_p1_0 (
  id int auto_increment,
  c1_t2_p1_0_id int,
  primary key (id),
  foreign key (c1_t2_p1_0_id) references c1_t2_p1_0 (id)
) engine=InnoDB default charset=utf8;