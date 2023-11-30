package com.zuji.remind.biz.enums;

import lombok.Getter;

/**
 * 日期类型.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 11:03
 **/
@Getter
public enum DateTypeEnum {
    /**
     * 阳历。
     */
    SOLAR_CALENDAR(1),

    /**
     * 农历。
     */
    LUNAR_CALENDAR(2),

    ;
    private final int type;

    DateTypeEnum(int type) {
        this.type = type;
    }

    public static DateTypeEnum getByType(int type) {
        for (DateTypeEnum dateTypeEnum : DateTypeEnum.values()) {
            if (dateTypeEnum.getType() == type) {
                return dateTypeEnum;
            }
        }
        throw new RuntimeException("暂不支持[" + type + "]类型");
    }
}
