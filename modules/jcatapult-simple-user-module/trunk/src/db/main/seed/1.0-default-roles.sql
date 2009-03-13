-- Insert the default roles and an admin user
insert into roles (name) values ('user'), ('admin');

insert into users (login, password, locked, expired, password_expired, partial, deleted) values ('admin@jcatapult.org', 'S42HhdOgrK0rLUNPnnJVKw==', false, false, false, false, false);
insert into users_roles (users_id, roles_id)
  select u.id, r.id from users u, roles r where u.login = 'admin@jcatapult.org' and r.name = 'admin'; 