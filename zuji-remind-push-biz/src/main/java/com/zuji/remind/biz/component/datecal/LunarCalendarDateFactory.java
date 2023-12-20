package com.zuji.remind.biz.component.datecal;

import cn.hutool.core.date.ChineseDate;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.zuji.remind.biz.enums.DateTypeEnum;
import org.apache.commons.lang3.tuple.ImmutablePair;

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
    public ImmutablePair<LocalDate, ChineseDate> analyzeCurrentNotifyDate(String storageDate, boolean isLeapMonth) {
        String[] dates = storageDate.split(StrUtil.DASHED);
        ChineseDate storageChineseDate = new ChineseDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]), isLeapMonth);
        LocalDate storageLocalDate = DateUtil.toLocalDateTime(storageChineseDate.getGregorianDate()).toLocalDate();
        return ImmutablePair.of(storageLocalDate, storageChineseDate);
    }

    @Override
    public ImmutablePair<LocalDate, ChineseDate> analyzeNextNotifyDate(String storageDate, boolean isLeapMonth) {
        LocalDate now = LocalDate.now();
        String[] dates = storageDate.split(StrUtil.DASHED);
        ChineseDate storageChineseDate = new ChineseDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]), isLeapMonth);
        int chineseYear = new ChineseDate(now).getChineseYear();

        ChineseDate nextNotifyChineseDate = new ChineseDate(chineseYear, storageChineseDate.getMonth(), storageChineseDate.getDay());
        LocalDate nextNotifyLocalDate = DateUtil.toLocalDateTime(nextNotifyChineseDate.getGregorianDate()).toLocalDate();
        // 如果日期已经过了，则计算下个日期
        if (nextNotifyLocalDate.isBefore(now)) {
            nextNotifyChineseDate = new ChineseDate(chineseYear + 1, nextNotifyChineseDate.getMonth(), nextNotifyChineseDate.getDay());
            nextNotifyLocalDate = DateUtil.toLocalDateTime(nextNotifyChineseDate.getGregorianDate()).toLocalDate();
        }
        return ImmutablePair.of(nextNotifyLocalDate, nextNotifyChineseDate);
    }
}
