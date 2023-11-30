package com.zuji.remind.biz.enums;

import lombok.Getter;

/**
 * 提醒方式枚举.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 23:00
 **/
@Getter
public enum RemindWayEnum {

    /**
     * 邮件。
     */
    EMAIL(1),

    /**
     * 钉钉。
     */
    DING_DING(2),

    /**
     * 微信。
     */
    WECHAT(3);

    private final int code;

    RemindWayEnum(int code) {
        this.code = code;
    }

    public static RemindWayEnum getByCode(int code) {
        for (RemindWayEnum wayEnum : RemindWayEnum.values()) {
            if (wayEnum.getCode() == code) {
                return wayEnum;
            }
        }
        throw new RuntimeException("暂不支持[" + code + "]类型");
    }
}
