package com.zuji.remind.biz.untils;

import java.time.DayOfWeek;

/**
 * 日期转换工具类.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-18 22:27
 **/
public class DateUtils {

    private DateUtils() {
    }


    /**
     * 获取星期几。
     *
     * @param dayOfWeek {@link DayOfWeek}
     */
    public static String week2Str(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "星期一";
            case TUESDAY:
                return "星期二";
            case WEDNESDAY:
                return "星期三";
            case THURSDAY:
                return "星期四";
            case FRIDAY:
                return "星期五";
            case SATURDAY:
                return "星期六";
            case SUNDAY:
                return "星期日";
            default:
                throw new RuntimeException("日期异常");
        }
    }
}
