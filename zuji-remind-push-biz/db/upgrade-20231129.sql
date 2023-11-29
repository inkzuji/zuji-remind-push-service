create table msg_push_way
(
    id           bigint        not null auto_increment comment '主键',
    push_type    tinyint       not null default 1 comment '推送类型: 1=邮箱;2=钉钉;3=微信',
    name         varchar(100)  not null default '' comment '名称',
    push_context varchar(1000) not null default '' comment '推送内容参数',
    update_time  datetime      not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time  datetime      not null default current_timestamp comment '创建时间',
    is_delete    tinyint       not null default '0' comment '是否删除：0=否，1=是',
    PRIMARY KEY (`id`)
)ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_general_ci
    COMMENT='推送方式';
