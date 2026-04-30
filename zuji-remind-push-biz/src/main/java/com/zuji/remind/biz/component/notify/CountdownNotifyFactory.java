package com.zuji.remind.biz.component.notify;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static com.zuji.remind.common.constant.CommonConstant.*;

/**
 * 倒计时通知抽象类实现.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 11:39
 **/
public class CountdownNotifyFactory extends AbstractNotifyFactory {
    @Override
    public NotifyBO analyzeIsNotify(LocalDate notifyDate, String remindTimes) {
        LocalDate now = LocalDate.now();
        if (notifyDate.isAfter(now)) {
            return new NotifyBO(false, MINUS_ONE_LONG);
        }

        if (notifyDate.isEqual(now)) {
            return new NotifyBO(true, ZERO_LONG);
        }
        long intervalDays = notifyDate.until(now, ChronoUnit.DAYS);
        for (String day : remindTimes.split(StrUtil.COMMA)) {
            if (NumberUtils.compare(intervalDays, Long.parseLong(day)) == ZERO_INT) {
                return new NotifyBO(true, intervalDays);
            }
        }

        // 特殊逻辑，每周一同送一次通知, 防止忘记
        if (SPECIAL_NOTIFY_WEEK.contains(now.getDayOfWeek())) {
            return new NotifyBO(true, intervalDays);
        }
        return new NotifyBO(false, intervalDays);
    }
}
