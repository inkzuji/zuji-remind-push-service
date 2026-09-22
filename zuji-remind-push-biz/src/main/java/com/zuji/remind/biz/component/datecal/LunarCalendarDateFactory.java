package com.zuji.remind.biz.component.datecal;

import cn.hutool.core.date.ChineseDate;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.zuji.remind.biz.enums.DateTypeEnum;

import java.time.LocalDate;

/**
 * 阴历日期服务实现.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 10:30
 **/
public class LunarCalendarDateFactory extends AbstractDateFactory {

    @Override
    public DateTypeEnum dateType() {
        return DateTypeEnum.LUNAR_CALENDAR;
    }

    @Override
    public DateBO calculateCurrentDate(String storageDate, boolean isLeapMonth) {
        String[] dates = storageDate.split(StrUtil.DASHED);
        ChineseDate storageChineseDate = new ChineseDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]), isLeapMonth);
        LocalDate storageLocalDate = DateUtil.toLocalDateTime(storageChineseDate.getGregorianDate()).toLocalDate();
        return new DateBO(storageLocalDate, storageChineseDate);
    }

    @Override
    public DateBO calculateNextDate(String storageDate, boolean isLeapMonth) {
        LocalDate now = LocalDate.now();
        String[] dates = storageDate.split(StrUtil.DASHED);
        int month = Integer.parseInt(dates[1]);
        int day = Integer.parseInt(dates[2]);
        int chineseYear = new ChineseDate(now).getChineseYear();

        // 每年提醒一次；目标年没有对应闰月时，四参数构造器按同名普通月计算。
        ChineseDate nextNotifyChineseDate = new ChineseDate(chineseYear, month, day, isLeapMonth);
        LocalDate nextNotifyLocalDate = DateUtil.toLocalDateTime(nextNotifyChineseDate.getGregorianDate()).toLocalDate();
        // 如果日期已经过了，则计算下个日期
        if (nextNotifyLocalDate.isBefore(now)) {
            nextNotifyChineseDate = new ChineseDate(chineseYear + 1, month, day, isLeapMonth);
            nextNotifyLocalDate = DateUtil.toLocalDateTime(nextNotifyChineseDate.getGregorianDate()).toLocalDate();
        }
        return new DateBO(nextNotifyLocalDate, nextNotifyChineseDate);
    }
}
