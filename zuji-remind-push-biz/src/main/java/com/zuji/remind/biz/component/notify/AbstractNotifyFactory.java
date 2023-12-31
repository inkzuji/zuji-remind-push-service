package com.zuji.remind.biz.component.notify;

import com.zuji.remind.biz.enums.EventTypeEnum;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * 通知抽象类.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 11:31
 **/
public abstract class AbstractNotifyFactory {
    protected static final List<DayOfWeek> SPECIAL_NOTIFY_WEEK = List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);

    public static AbstractNotifyFactory getInstance(EventTypeEnum typeEnum) {
        switch (typeEnum) {
            case BIRTHDAY:
            case ANNIVERSARY:
                return new FrequencyNotifyFactory();
            case COUNTDOWN:
                return new CountdownNotifyFactory();
            default:
                throw new RuntimeException("暂不支持[" + typeEnum + "]类型");
        }
    }

    /**
     * 分析是否需要通知。
     *
     * @param notifyDate  通知日期
     * @param remindTimes 通知频率，多个通知频率逗号隔开
     * @return left=true通知， right=剩余天数
     */
    public abstract ImmutablePair<Boolean, Long> analyzeIsNotify(LocalDate notifyDate, String remindTimes);
}
