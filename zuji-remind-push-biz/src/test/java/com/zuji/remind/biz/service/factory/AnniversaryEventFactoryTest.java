package com.zuji.remind.biz.service.factory;

import com.zuji.remind.biz.enums.DateTypeEnum;
import com.zuji.remind.biz.enums.EnableStatusEnum;
import com.zuji.remind.biz.enums.EventTypeEnum;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.model.bo.AggreNotifyBO;
import com.zuji.remind.biz.model.bo.EventContextBO;
import com.zuji.remind.biz.model.bo.MemorialDayTaskBO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

class AnniversaryEventFactoryTest {

    @Test
    void anniversaryEmailUsesYearsAcrossALeapYear() {
        LocalDate today = LocalDate.of(2024, 9, 22);
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(today);
            EventContextBO context = context(LocalDate.of(2023, 9, 22), 0L);
            assertEquals("<p>相识</p><p>1周年快乐！</p>", new AnniversaryEventFactory().getEmailMsgContent(context));
        }
    }

    @Test
    void nonAnniversaryEmailStillUsesElapsedDays() {
        LocalDate today = LocalDate.of(2024, 9, 22);
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(today);
            EventContextBO context = context(LocalDate.of(2023, 9, 23), 1L);
            assertEquals("<p>相识</p><p>已经365天了！</p>", new AnniversaryEventFactory().getEmailMsgContent(context));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "2024-09-17, 2023-08-15, false",
            "2024-12-31, 2023-12-01, false",
            "2024-03-10, 2023-02-01, true",
            "2025-01-28, 2023-12-30, false"
    })
    void lunarAnniversaryUsesLunarYearsForBothChannels(LocalDate today, String storedDate, boolean leapMonth) {
        MemorialDayTaskBO task = lunarTask(storedDate, leapMonth);
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(today);
            List<AggreNotifyBO> messages = new AnniversaryEventFactory().dealWithData(task);

            assertEquals(2, messages.size());
            assertEquals(RemindWayEnum.EMAIL, messages.get(0).getRemindWayEnum());
            assertEquals("<p>相识</p><p>1周年快乐！</p>", messages.get(0).getMsg());
            assertEquals(RemindWayEnum.DING_DING, messages.get(1).getRemindWayEnum());
            assertTrue(messages.get(1).getMsg().endsWith("1周年快乐！"));
        }
    }

    @Test
    void lunarAdvanceReminderStillUsesElapsedSolarDaysForBothChannels() {
        LocalDate today = LocalDate.of(2024, 9, 16);
        MemorialDayTaskBO task = lunarTask("2023-08-15", false);
        task.setRemindTimes("1");
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(today);
            List<AggreNotifyBO> messages = new AnniversaryEventFactory().dealWithData(task);

            assertEquals(2, messages.size());
            assertEquals("<p>相识</p><p>已经353天了！</p>", messages.get(0).getMsg());
            assertTrue(messages.get(1).getMsg().endsWith("已经**353**天了！"));
        }
    }

    private MemorialDayTaskBO lunarTask(String storedDate, boolean leapMonth) {
        MemorialDayTaskBO task = new MemorialDayTaskBO();
        task.setName("相识");
        task.setEventType(EventTypeEnum.ANNIVERSARY);
        task.setDateType(DateTypeEnum.LUNAR_CALENDAR);
        task.setMemorialDate(storedDate);
        task.setIsLeapMonth(leapMonth ? EnableStatusEnum.ENABLE : EnableStatusEnum.DISABLE);
        task.setStatusRemind(EnableStatusEnum.ENABLE);
        task.setRemindTimes("0");
        task.setRemindWays(List.of(RemindWayEnum.EMAIL, RemindWayEnum.DING_DING));
        return task;
    }

    private EventContextBO context(LocalDate recordDate, long intervalDays) {
        EventContextBO context = new EventContextBO();
        EventContextBO.OriginalDB original = new EventContextBO.OriginalDB();
        original.setName("相识");
        context.setOriginalDB(original);
        EventContextBO.CalculateResultBO result = EventContextBO.CalculateResultBO.instance();
        result.setRecordDate(recordDate);
        result.setIntervalDays(intervalDays);
        context.setCalculateResultBO(result);
        return context;
    }
}
