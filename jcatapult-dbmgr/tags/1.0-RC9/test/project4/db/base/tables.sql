create table p4_t1 (
  id int auto_increment,
  c1_t1_id int not null,
  primary key (id),
  foreign key (c1_t1_id) references c1_t1 (id)
) engine=InnoDB default charset=utf8;