package com.zuji.remind.biz.component.datecal;

import cn.hutool.core.date.ChineseDate;
import com.zuji.remind.biz.enums.DateTypeEnum;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.LocalDate;

/**
 * 日期抽象类.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 11:02
 **/
public abstract class AbstractDateFactory {

    public static AbstractDateFactory getInstance(DateTypeEnum type) {
        switch (type) {
            case SOLAR_CALENDAR:
                return new SolarCalendarDateFactory();
            case LUNAR_CALENDAR:
                return new LunarCalendarDateFactory();
            default:
                throw new RuntimeException("暂不支持[" + type + "]类型");
        }
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
    public abstract ImmutablePair<LocalDate, ChineseDate> analyzeCurrentNotifyDate(String storageDate, boolean isLeapMonth);

    /**
     * 计算下一个通知日期。
     * <p>默认每年一次</p>
     *
     * @param storageDate 数据库存储日期,格式: yyyy-MM-dd.
     * @param isLeapMonth 是否是闰月
     * @return left=阳历, right=阴历
     */
    public abstract ImmutablePair<LocalDate, ChineseDate> analyzeNextNotifyDate(String storageDate, boolean isLeapMonth);
}
