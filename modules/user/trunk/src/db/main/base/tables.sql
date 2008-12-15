create table addresses (
    id integer not null auto_increment,
    city varchar(255) not null,
    country varchar(255) not null,
    district varchar(255),
    postal_code varchar(255),
    state varchar(255),
    street varchar(512) not null,
    type varchar(255) not null,
    primary key (id)
) ENGINE=InnoDB;

create table email_addresses (
    id integer not null auto_increment,
    address varchar(255) not null,
    display varchar(255),
    type varchar(255) not null,
    primary key (id)
) ENGINE=InnoDB;

create table credit_cards (
    id integer not null auto_increment,
    city varchar(255) not null,
    country varchar(255) not null,
    district varchar(255),
    postal_code varchar(255),
    state varchar(255),
    street varchar(255) not null,
    expiration_month integer not null,
    expiration_year integer not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    number varchar(20) not null,
    verified bit not null,
    insert_date datetime,
    insert_user varchar(255),
    update_date datetime,
    update_user varchar(255),
    users_id integer,
    primary key (id)
) ENGINE=InnoDB;

create table phone_numbers (
    id integer not null auto_increment,
    number varchar(255) not null,
    type varchar(255) not null,
    primary key (id)
) ENGINE=InnoDB;

create table roles (
    id integer not null auto_increment,
    name varchar(255) not null unique,
    primary key (id)
) ENGINE=InnoDB;

create table user_properties (
    id integer not null auto_increment,
    name varchar(255) not null,
    value varchar(255) not null,
    users_id integer,
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
    primary key (id)
) ENGINE=InnoDB;

create table users_addresses (
    users_id integer not null,
    addresses_id integer not null,
    primary key (users_id, addresses_id),
    unique (addresses_id)
) ENGINE=InnoDB;

create table users_email_addresses (
    users_id integer not null,
    email_addresses_id integer not null,
    primary key (users_id, email_addresses_id),
    unique (email_addresses_id)
) ENGINE=InnoDB;

create table users_phone_numbers (
    users_id integer not null,
    phone_numbers_id integer not null,
    primary key (users_id, phone_numbers_id),
    unique (phone_numbers_id)
) ENGINE=InnoDB;

create table users_roles (
    users_id integer not null,
    roles_id integer not null
) ENGINE=InnoDB;

alter table credit_cards
    add index FKCEAB169DADD4BEB0 (users_id),
    add constraint FKCEAB169DADD4BEB0
    foreign key (users_id)
    references users (id);

alter table user_properties
    add index FK18623A27ADD4BEB0 (users_id),
    add constraint FK18623A27ADD4BEB0
    foreign key (users_id)
    references users (id);

alter table users_addresses
    add index FKCCD41A2BA6C59753 (users_id),
    add constraint FKCCD41A2BA6C59753
    foreign key (users_id)
    references users (id);

alter table users_addresses
    add index FKCCD41A2BAD9ABC41 (addresses_id),
    add constraint FKCCD41A2BAD9ABC41
    foreign key (addresses_id)
    references addresses (id);

alter table users_email_addresses
    add index uea_uid_idx (users_id),
    add constraint uea_uid_fk
    foreign key (users_id)
    references users (id);

alter table users_email_addresses
    add index uea_eaid_idx (email_addresses_id),
    add constraint uea_eaid_fk
    foreign key (email_addresses_id)
    references email_addresses (id);

alter table users_phone_numbers
    add index FK55C7568258972BED (phone_numbers_id),
    add constraint FK55C7568258972BED
    foreign key (phone_numbers_id)
    references phone_numbers (id);

alter table users_phone_numbers
    add index FK55C75682A6C59753 (users_id),
    add constraint FK55C75682A6C59753
    foreign key (users_id)
    references users (id);

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
