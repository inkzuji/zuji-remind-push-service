package com.zuji.remind.biz.service.factory;

import com.zuji.remind.biz.enums.DateTypeEnum;
import com.zuji.remind.biz.enums.EnableStatusEnum;
import com.zuji.remind.biz.enums.EventTypeEnum;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.model.bo.AggreNotifyBO;
import com.zuji.remind.biz.model.bo.MemorialDayTaskBO;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

class BirthdayEventFactoryRegressionTest {

    @Test
    void lunarBirthdayOnThirtyProducesBothMessagesOnTheSmallMonthsLastDay() {
        LocalDate today = LocalDate.of(2021, 10, 5);
        MemorialDayTaskBO task = lunarTask("2020-08-30");

        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(today);
            List<AggreNotifyBO> messages = new BirthdayEventFactory().dealWithData(task);

            assertEquals(2, messages.size());
            assertEquals(RemindWayEnum.EMAIL, messages.get(0).getRemindWayEnum());
            assertEquals("<p>生日:生日测试</p><p>生日快乐！</p>", messages.get(0).getMsg());
            assertEquals(RemindWayEnum.DING_DING, messages.get(1).getRemindWayEnum());
            assertEquals("### 生日测试  \n  **生日**: 2021-10-05 星期二 (八月廿九)  \n  生日快乐！", messages.get(1).getMsg());
        }
    }

    @Test
    void lunarThirtyAdvanceReminderUsesUpcomingDateAndPositiveDays() {
        LocalDate today = LocalDate.of(2025, 12, 1);
        MemorialDayTaskBO task = lunarTask("2023-12-30");

        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(today);
            List<AggreNotifyBO> messages = new BirthdayEventFactory().dealWithData(task);

            assertEquals(2, messages.size());
            assertEquals(RemindWayEnum.EMAIL, messages.get(0).getRemindWayEnum());
            assertEquals("<p>生日:生日测试</p>距离生日还有**77**天！", messages.get(0).getMsg());
            assertEquals(RemindWayEnum.DING_DING, messages.get(1).getRemindWayEnum());
            assertEquals("### 生日测试  \n  **生日**: 2026-02-16 星期一 (腊月廿九)  \n  距离生日还有**77**天！", messages.get(1).getMsg());
        }
    }

    private MemorialDayTaskBO lunarTask(String storedDate) {
        MemorialDayTaskBO task = new MemorialDayTaskBO();
        task.setName("生日测试");
        task.setEventType(EventTypeEnum.BIRTHDAY);
        task.setDateType(DateTypeEnum.LUNAR_CALENDAR);
        task.setMemorialDate(storedDate);
        task.setIsLeapMonth(EnableStatusEnum.DISABLE);
        task.setStatusRemind(EnableStatusEnum.ENABLE);
        task.setRemindTimes("0");
        task.setRemindWays(List.of(RemindWayEnum.EMAIL, RemindWayEnum.DING_DING));
        return task;
    }
}
