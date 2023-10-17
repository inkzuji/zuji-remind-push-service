CREATE TABLE `memorial_day_task`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `event_type`    tinyint      NOT NULL DEFAULT '1' COMMENT '事件类型:1=生日; 2=纪念日; 3=倒计时;',
    `name`          varchar(50)  NOT NULL DEFAULT '' COMMENT '名称',
    `task_desc`     varchar(250) NOT NULL DEFAULT '' COMMENT '描述',
    `date_type`     tinyint      NOT NULL DEFAULT '1' COMMENT '日期类型: 1=阳历; 2=农历',
    `memorial_date` varchar(126) NOT NULL DEFAULT '' COMMENT '日期',
    `is_leap_month` tinyint      NOT NULL DEFAULT '0' COMMENT '是否闰月：0=否；1=是',
    `status_remind` tinyint      NOT NULL DEFAULT '0' COMMENT '是否提醒: 0=提醒; 1=不提醒;',
    `remind_times`  varchar(250) NOT NULL DEFAULT '' COMMENT '提醒频率',
    `remind_way`    varchar(32)  NOT NULL DEFAULT '0' COMMENT '提醒方式: 1=邮箱;2=钉钉;3=微信',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_delete`     tinyint      NOT NULL DEFAULT '0' COMMENT '是否删除: 0=否; 1=是;',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='倒数记录任务';