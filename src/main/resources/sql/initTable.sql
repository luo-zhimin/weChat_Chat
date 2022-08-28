create table if not exists config
(
    id           bigint auto_increment
    primary key,
    config_key   varchar(255) not null,
    config_value varchar(255) not null,
    remark       varchar(255) not null,
    constraint ux_key
    unique (config_key)
    );

create table if not exists user
(
    id          bigint auto_increment comment 'id'
    primary key,
    name        varchar(255)                       null comment '真实姓名',
    user_name   varchar(100)                       not null comment '用户名',
    password    varchar(100)                       not null comment '密码',
    mobile      varchar(11)                        null comment '手机号',
    email       varchar(100)                       null comment '邮箱',
    is_delete   int      default 0                 not null,
    create_time datetime                           not null,
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
    )
    comment '用户表';

create index user_admin_user_name_IDX
    on user (user_name);

create table if not exists wechat_user
(
    id          bigint auto_increment
    primary key,
    open_id     varchar(100)      not null comment 'open Id',
    union_id    varchar(100)      null,
    user_info   mediumtext        null comment '用户信息',
    user_type   tinyint default 0 not null comment '用户类型',
    relation_id bigint            not null comment '关系Id',
    is_del      int     default 0 not null comment '是否删除',
    create_time datetime          not null,
    constraint ux_code_del
    unique (open_id, is_del)
    );

