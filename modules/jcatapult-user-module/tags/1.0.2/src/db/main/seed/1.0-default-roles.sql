-- Insert the default roles and an admin user
insert into roles (name) values ('user'), ('admin');

insert into users (username, email, password, locked, expired, password_expired, partial, verified, insert_user, insert_date, update_user, update_date, deleted)
  values ('admin@jcatapult.org', 'admin@jcatapult.org', 'S42HhdOgrK0rLUNPnnJVKw==', false, false, false, false, true, 'seed', now(), 'seed', now(), false);
insert into users_roles (users_id, roles_id)
  select u.id, r.id from users u, roles r where u.username = 'admin@jcatapult.org' and r.name = 'admin'; 