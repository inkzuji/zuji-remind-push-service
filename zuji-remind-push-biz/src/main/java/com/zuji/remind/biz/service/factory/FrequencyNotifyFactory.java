package com.zuji.remind.biz.service.factory;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static com.zuji.remind.common.constant.CommonConstant.ZERO_INT;
import static com.zuji.remind.common.constant.CommonConstant.ZERO_LONG;

/**
 * 频率通知抽象类实现.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-10-18 11:39
 **/
public class FrequencyNotifyFactory extends AbstractNotifyFactory {
    @Override
    public ImmutablePair<Boolean, Long> analyzeIsNotify(LocalDate notifyDate, String remindTimes) {
        LocalDate now = LocalDate.now();
        if (notifyDate.isEqual(now)) {
            return ImmutablePair.of(true, ZERO_LONG);
        }

        long intervalDays = now.until(notifyDate, ChronoUnit.DAYS);
        for (String day : remindTimes.split(StrUtil.COMMA)) {
            if (NumberUtils.compare(intervalDays, Long.parseLong(day)) == ZERO_INT) {
                return ImmutablePair.of(true, intervalDays);
            }
        }
        return ImmutablePair.of(false, intervalDays);
    }
}
