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
    BIRTHDAY(1, "生日", "birthdayEventFactory"),
    ANNIVERSARY(2, "纪念日", "anniversaryEventFactory"),
    COUNTDOWN(3, "倒计时", "countdownEventFactory");

    private final int code;
    private final String desc;
    private final String factoryName;

    EventTypeEnum(int code, String desc, String factoryName) {
        this.code = code;
        this.desc = desc;
        this.factoryName = factoryName;
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
