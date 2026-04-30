package com.zuji.remind.biz.component.datecal;

import cn.hutool.core.date.ChineseDate;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.zuji.remind.biz.enums.DateTypeEnum;

import java.time.LocalDate;

import static com.zuji.remind.common.constant.CommonConstant.DEFAULT_DATE_FORMAT;
import static com.zuji.remind.common.constant.CommonConstant.ONE_LONG;

/**
 * 阳历日期服务实现.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 10:29
 **/
public class SolarCalendarDateFactory extends AbstractDateFactory {

    @Override
    public DateTypeEnum dateType() {
        return DateTypeEnum.SOLAR_CALENDAR;
    }

    @Override
    public DateBO calculateCurrentDate(String storageDate, boolean isLeapMonth) {
        LocalDate storageLocalDate = LocalDateTimeUtil.parseDate(storageDate, DEFAULT_DATE_FORMAT);
        ChineseDate chineseDate = new ChineseDate(storageLocalDate);
        return new DateBO(storageLocalDate, chineseDate);
    }

    @Override
    public DateBO calculateNextDate(String storageDate, boolean isLeapMonth) {
        LocalDate now = LocalDate.now();
        LocalDate storageLocalDate = LocalDateTimeUtil.parseDate(storageDate, DEFAULT_DATE_FORMAT);
        LocalDate nextLocalDate = storageLocalDate.withYear(now.getYear());

        // 如果今年通知日期已经过了
        if (nextLocalDate.isBefore(now)) {
            nextLocalDate = nextLocalDate.plusYears(ONE_LONG);
        }
        ChineseDate chineseDate = new ChineseDate(nextLocalDate);
        return new DateBO(nextLocalDate, chineseDate);
    }
}
