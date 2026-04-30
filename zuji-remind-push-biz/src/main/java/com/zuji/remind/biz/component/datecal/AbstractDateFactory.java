package com.zuji.remind.biz.component.datecal;

import cn.hutool.core.date.ChineseDate;
import com.zuji.remind.biz.enums.DateTypeEnum;

import java.time.LocalDate;

/**
 * 日期抽象类.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 11:02
 **/
public abstract class AbstractDateFactory {

    /**
     * 根据日期类型获取对应的日期计算工厂实例。
     *
     * @param type 日期类型枚举
     * @return 日期计算工厂实例
     */
    public static AbstractDateFactory getInstance(DateTypeEnum type) {
        return switch (type) {
            case SOLAR_CALENDAR -> new SolarCalendarDateFactory();
            case LUNAR_CALENDAR -> new LunarCalendarDateFactory();
            default -> throw new RuntimeException("暂不支持[" + type + "]类型");
        };
    }

    /**
     * 日期类型。
     *
     * @return {@link DateTypeEnum}
     */
    public abstract DateTypeEnum dateType();

    /**
     * 计算当前通知日期农历阳历。
     *
     * @param storageDate 数据库存储日期,格式: yyyy-MM-dd.
     * @param isLeapMonth 是否是闰月
     * @return left=阳历, right=阴历
     */
    public abstract DateBO calculateCurrentDate(String storageDate, boolean isLeapMonth);

    /**
     * 计算下一个通知日期。
     * <p>默认每年一次</p>
     *
     * @param storageDate 数据库存储日期,格式: yyyy-MM-dd.
     * @param isLeapMonth 是否是闰月
     * @return left=阳历, right=阴历
     */
    public abstract DateBO calculateNextDate(String storageDate, boolean isLeapMonth);

    public record DateBO(LocalDate solarDate, ChineseDate lunarDate) {
    }

}
