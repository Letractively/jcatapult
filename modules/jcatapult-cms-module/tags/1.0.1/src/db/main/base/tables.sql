create table cms_content_states (
    id integer not null auto_increment,
    insert_date datetime not null,
    update_date datetime not null,
    insert_user varchar(255) not null,
    update_user varchar(255) not null,
    visible bit,
    cms_contents_id integer not null,
    primary key (id)
) ENGINE=InnoDB;

create table cms_contents (
    id integer not null auto_increment,
    insert_date datetime not null,
    update_date datetime not null,
    insert_user varchar(255) not null,
    update_user varchar(255) not null,
    content mediumtext not null,
    locale varchar(255) not null,
    cms_nodes_id integer not null,
    cms_locale_contents_id integer not null,
    cms_node_actions_id integer not null,
    primary key (id),
    unique (cms_node_actions_id)
) ENGINE=InnoDB;

create table cms_current_contents (
    cms_nodes_id integer not null,
    cms_contents_id integer not null,
    primary key (cms_nodes_id, cms_contents_id),
    unique (cms_contents_id)
) ENGINE=InnoDB;

create table cms_locale_contents (
    id integer not null auto_increment,
    insert_date datetime not null,
    update_date datetime not null,
    insert_user varchar(255) not null,
    update_user varchar(255) not null,
    locale varchar(255) not null,
    cms_nodes_id integer not null,
    primary key (id)
) ENGINE=InnoDB;

create table cms_node_action_states (
    id integer not null auto_increment,
    insert_date datetime not null,
    update_date datetime not null,
    insert_user varchar(255) not null,
    update_user varchar(255) not null,
    comment varchar(255),
    state varchar(255) not null,
    user_id integer not null,
    cms_node_actions_id integer not null,
    primary key (id)
) ENGINE=InnoDB;

create table cms_node_actions (
    id integer not null auto_increment,
    insert_date datetime not null,
    update_date datetime not null,
    insert_user varchar(255) not null,
    update_user varchar(255) not null,
    current_state varchar(255) not null,
    description varchar(4000),
    type varchar(255) not null,
    user_id integer not null,
    cms_nodes_id integer not null,
    primary key (id)
) ENGINE=InnoDB;

create table cms_node_states (
    id integer not null auto_increment,
    insert_date datetime not null,
    update_date datetime not null,
    insert_user varchar(255) not null,
    update_user varchar(255) not null,
    visible bit,
    cms_nodes_id integer not null,
    primary key (id)
) ENGINE=InnoDB;

create table cms_nodes (
    type varchar(31) not null,
    id integer not null auto_increment,
    insert_date datetime not null,
    update_date datetime not null,
    insert_user varchar(255) not null,
    update_user varchar(255) not null,
    localName varchar(255) not null,
    uid varchar(255) not null unique,
    visible bit,
    content_type varchar(255),
    parent_id integer,
    primary key (id)
) ENGINE=InnoDB;

alter table cms_content_states
    add index FK27E35E3EC1F8E72A (cms_contents_id),
    add constraint FK27E35E3EC1F8E72A
    foreign key (cms_contents_id)
    references cms_contents (id);

alter table cms_contents
    add index FKFFA56D5066C50E81 (cms_nodes_id),
    add constraint FKFFA56D5066C50E81
    foreign key (cms_nodes_id)
    references cms_nodes (id);

alter table cms_contents
    add index FKFFA56D5022E1A877 (cms_locale_contents_id),
    add constraint FKFFA56D5022E1A877
    foreign key (cms_locale_contents_id)
    references cms_locale_contents (id);

alter table cms_contents
    add index FKFFA56D5051456835 (cms_node_actions_id),
    add constraint FKFFA56D5051456835
    foreign key (cms_node_actions_id)
    references cms_node_actions (id);

alter table cms_current_contents
    add index FK11B7635666C50E81 (cms_nodes_id),
    add constraint FK11B7635666C50E81
    foreign key (cms_nodes_id)
    references cms_nodes (id);

alter table cms_current_contents
    add index FK11B76356C1F8E72A (cms_contents_id),
    add constraint FK11B76356C1F8E72A
    foreign key (cms_contents_id)
    references cms_contents (id);

alter table cms_locale_contents
    add index FK4031286966C50E81 (cms_nodes_id),
    add constraint FK4031286966C50E81
    foreign key (cms_nodes_id)
    references cms_nodes (id);

alter table cms_node_action_states
    add index FKF0B91F6451456835 (cms_node_actions_id),
    add constraint FKF0B91F6451456835
    foreign key (cms_node_actions_id)
    references cms_node_actions (id);

alter table cms_node_actions
    add index FK879482F6AB3022BA (cms_nodes_id),
    add constraint FK879482F6AB3022BA
    foreign key (cms_nodes_id)
    references cms_nodes (id);

alter table cms_node_states
    add index FK23FDF0E9AB3022BA (cms_nodes_id),
    add constraint FK23FDF0E9AB3022BA
    foreign key (cms_nodes_id)
    references cms_nodes (id);

alter table cms_nodes
    add index FKFD221C3BA3D7EE9A (parent_id),
    add constraint FKFD221C3BA3D7EE9A
    foreign key (parent_id)
    references cms_nodes (id);

alter table cms_nodes
    add index FKFD221C3B98DC4F52 (parent_id),
    add constraint FKFD221C3B98DC4F52
    foreign key (parent_id)
    references cms_nodes (id);

alter table cms_nodes
    add index FKFD221C3BC2222BEB (parent_id),
    add constraint FKFD221C3BC2222BEB
    foreign key (parent_id)
    references cms_nodes (id);