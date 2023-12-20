create table msg_push_task
(
    id          bigint   not null auto_increment comment '主键',
    msg_type    tinyint  not null default 1 comment '推送类型: 1=邮箱;2=钉钉;3=微信',
    msg_request  text comment '消息内容',
    msg_response text comment '消息发送结果',
    status      tinyint  not null default 0 comment '发送状态：0=待发送，1=发送中，2=已发送，3=发送失败,待重试，4=发送失败',
    fail_num     int not null default 0 comment '失败次数',
    msg_index   int      not null default 0 comment '消息标记点位，用于定时删除',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    PRIMARY KEY (`id`),
    key `idx_status` (`status`) using btree,
    key `idx_msg_index` (`msg_index`) using btree
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
    COMMENT ='消息推送任务';
