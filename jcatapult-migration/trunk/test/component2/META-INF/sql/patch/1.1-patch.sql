create table bar_patch1_1 (
  id int auto_increment,
  foo_patch1_1_id int not null,
  primary key (id),
  foreign key fk_foo_path1_1 (foo_patch1_1_id) references foo_patch1_1(id)
) engine=InnoDB default charset=utf8;