create table bar_patch1_0 (
  id int auto_increment,
  foo_patch1_0_id int not null,
  primary key (id),
  foreign key fk_foo_path1_0 (foo_patch1_0_id) references foo_patch1_0(id)
) engine=InnoDB default charset=utf8;