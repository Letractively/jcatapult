-- Insert the default roles and an admin user
insert into roles (name) values ('user'), ('admin');

insert into users (email, password, locked, expired, password_expired, partial, verified, deleted)
  values ('admin@jcatapult.org', 'S42HhdOgrK0rLUNPnnJVKw==', false, false, false, false, true, false);
insert into users_roles (users_id, roles_id)
  select u.id, r.id from users u, roles r where u.email= 'admin@jcatapult.org' and r.name = 'admin'; 