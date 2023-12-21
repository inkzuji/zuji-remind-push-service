package com.zuji.remind.biz.enums;

import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

/**
 * 消息推送任务状态.
 * 发送状态：0=待发送，1=发送中，2=已发送，3=发送失败,待重试，4=发送失败
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-21 13:54
 **/
@Getter
public enum TaskStatusEnum {
    /**
     * 待发送。
     */
    WAITING_SEND(0),

    /**
     * 发送中。
     */
    SENDING(1),

    /**
     * 发送成功。
     */
    SUCCESS(2),

    /**
     * 发送失败，待重试。
     */
    FAIL_TRIED_AGAIN_SEND(3),

    /**
     * 发送失败。
     */
    FAIL_SEND(4),
    ;
    private final int code;

    TaskStatusEnum(int code) {
        this.code = code;
    }

    public static TaskStatusEnum getByCode(int code) {
        return Stream.of(TaskStatusEnum.values())
                .filter(v -> v.getCode() == code)
                .findAny()
                .orElse(null);
    }

    /**
     * 推送消息定时任务查询状态列表。
     */
    public static final List<Integer> PUSH_MSG_SCHEDULER_STATUS_LIST = List.of(
            WAITING_SEND.getCode(),
            FAIL_TRIED_AGAIN_SEND.getCode());
}
