alter table memorial_day_task
    modify status_remind tinyint default 0 not null comment '是否提醒: 0=不提醒; 1=提醒;';