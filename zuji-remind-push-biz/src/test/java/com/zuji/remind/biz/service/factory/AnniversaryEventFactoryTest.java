package com.zuji.remind.biz.service.factory;

import com.zuji.remind.biz.model.bo.EventContextBO;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
