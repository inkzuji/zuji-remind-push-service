package com.zuji.remind.biz.component.datecal;

import com.zuji.remind.biz.component.notify.AbstractNotifyFactory;
import com.zuji.remind.biz.component.notify.FrequencyNotifyFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

class SolarCalendarDateFactoryTest {

    @Test
    void rolloverRestoresLeapDayFromOriginalDate() {
        assertNextDate(LocalDate.of(2023, 12, 1), LocalDate.of(2024, 2, 29));
    }

    @Test
    void commonYearUsesFebruaryTwentyEighth() {
        assertNextDate(LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 28));
    }

    @Test
    void leapYearKeepsFebruaryTwentyNinth() {
        assertNextDate(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29));
    }

    @Test
    void keepsTodaysOccurrenceInCommonAndLeapYears() {
        assertNextDate(LocalDate.of(2023, 2, 28), LocalDate.of(2023, 2, 28));
        assertNextDate(LocalDate.of(2024, 2, 29), LocalDate.of(2024, 2, 29));
    }

    @Test
    void rolloverToCommonYearUsesFebruaryTwentyEighth() {
        assertNextDate(LocalDate.of(2024, 3, 1), LocalDate.of(2025, 2, 28));
    }

    @Test
    void advanceReminderAcrossYearBoundaryUsesCorrectRemainingDays() {
        LocalDate today = LocalDate.of(2023, 12, 2);
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(today);
            AbstractDateFactory.DateBO next = new SolarCalendarDateFactory().calculateNextDate("2020-02-29", false);
            AbstractNotifyFactory.NotifyBO notification = new FrequencyNotifyFactory().analyzeIsNotify(next.solarDate(), "89");

            assertEquals(89L, notification.days());
            assertTrue(notification.isNotify());
        }
    }

    private void assertNextDate(LocalDate today, LocalDate expected) {
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(today);
            AbstractDateFactory.DateBO result = new SolarCalendarDateFactory().calculateNextDate("2020-02-29", false);
            assertEquals(expected, result.solarDate());
        }
    }
}
