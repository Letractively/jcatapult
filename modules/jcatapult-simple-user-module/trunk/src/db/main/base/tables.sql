create table roles (
    id integer not null auto_increment,
    name varchar(255) not null unique,
    primary key (id)
) ENGINE=InnoDB;

create table users (
    id integer not null auto_increment,
    deleted bit not null,
    company_name varchar(255),
    first_name varchar(255),
    middle_name varchar(255),
    last_name varchar(255),
    maiden_name varchar(255),
    prefix varchar(255),
    suffix varchar(255),
    nickname varchar(255),
    expired bit not null,
    guid varchar(255),
    locked bit not null,
    login varchar(255) not null unique,
    password varchar(255) not null,
    password_expired bit not null,
    partial bit,
    primary key (id)
) ENGINE=InnoDB;

create table users_roles (
    users_id integer not null,
    roles_id integer not null
) ENGINE=InnoDB;

alter table users_roles
    add index FKF6CCD9C6A6C27329 (roles_id),
    add constraint FKF6CCD9C6A6C27329
    foreign key (roles_id)
    references roles (id);

alter table users_roles
    add index FKF6CCD9C6A6C59753 (users_id),
    add constraint FKF6CCD9C6A6C59753
    foreign key (users_id)
    references users (id);
