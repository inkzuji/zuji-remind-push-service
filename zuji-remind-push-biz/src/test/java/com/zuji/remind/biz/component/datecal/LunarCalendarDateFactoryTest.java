package com.zuji.remind.biz.component.datecal;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

class LunarCalendarDateFactoryTest {

    @Test
    void ordinaryMonthDoesNotBecomeALeapMonth() {
        assertNextDate(LocalDate.of(2023, 2, 1), "2020-02-01", false, LocalDate.of(2023, 2, 20), false);
    }

    @Test
    void preservesLeapMonthWhenTheTargetYearHasIt() {
        assertNextDate(LocalDate.of(2023, 2, 1), "2023-02-01", true, LocalDate.of(2023, 3, 22), true);
    }

    @Test
    void fallsBackToOrdinaryMonthWhenTargetYearHasNoMatchingLeapMonth() {
        assertNextDate(LocalDate.of(2026, 9, 22), "2023-02-01", true, LocalDate.of(2027, 3, 8), false);
    }

    @Test
    void rolloverUsesOriginalMonthAndLeapFlag() {
        assertNextDate(LocalDate.of(2022, 4, 1), "2023-02-01", true, LocalDate.of(2023, 3, 22), true);
    }

    @Test
    void keepsTheOccurrenceOnToday() {
        assertNextDate(LocalDate.of(2023, 3, 22), "2023-02-01", true, LocalDate.of(2023, 3, 22), true);
    }

    private void assertNextDate(LocalDate today, String storedDate, boolean leapMonth, LocalDate expected, boolean expectedLeapMonth) {
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(today);
            AbstractDateFactory.DateBO result = new LunarCalendarDateFactory().calculateNextDate(storedDate, leapMonth);
            assertEquals(expected, result.solarDate());
            assertEquals(expectedLeapMonth, result.lunarDate().isLeapMonth());
            assertEquals(expectedLeapMonth ? 3 : 2, result.lunarDate().getMonth());
            assertEquals(1, result.lunarDate().getDay());
        }
    }
}
