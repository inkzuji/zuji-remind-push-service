package com.zuji.remind.biz.enums;

import lombok.Getter;

/**
 * 事件类型.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-12 21:14
 **/
@Getter
public enum EventTypeEnum {
    BIRTHDAY(1, "生日"),
    ANNIVERSARY(2, "纪念日"),
    COUNTDOWN(3, "倒计时");

    private final int code;
    private final String desc;

    EventTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EventTypeEnum getByCode(int code) {
        for (EventTypeEnum typeEnum : EventTypeEnum.values()) {
            if (typeEnum.getCode() == code) {
                return typeEnum;
            }
        }
        throw new RuntimeException("暂不支持" + code + "类型");
    }
}
