insert into roles (name) values ('publisher'), ('editor');

insert into users (login, password, expired, locked, password_expired, deleted, partial) values
        ('publisher@jcatapult.org', 'S42HhdOgrK0rLUNPnnJVKw==', false, false, false, false, false),
        ('editor@jcatapult.org', 'S42HhdOgrK0rLUNPnnJVKw==', false, false, false, false, false);

insert into users_roles (users_id, roles_id)
        select u.id, r.id from users u, roles r where u.login = 'publisher@jcatapult.org' and r.name = 'publisher';
insert into users_roles (users_id, roles_id)
        select u.id, r.id from users u, roles r where u.login = 'publisher@jcatapult.org' and r.name = 'admin';

insert into users_roles (users_id, roles_id)
        select u.id, r.id from users u, roles r where u.login = 'editor@jcatapult.org' and r.name = 'editor';
insert into users_roles (users_id, roles_id)
        select u.id, r.id from users u, roles r where u.login = 'editor@jcatapult.org' and r.name = 'admin';