package com.zuji.remind.biz.component.datecal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest
    @CsvSource({
            "2021-09-01, 2020-08-30, false, 2021-10-05, 2021, 8, 29, false",
            "2021-10-05, 2020-08-30, false, 2021-10-05, 2021, 8, 29, false",
            "2021-10-06, 2020-08-30, false, 2022-09-25, 2022, 8, 30, false",
            "2025-07-01, 2017-06-30, true, 2025-08-22, 2025, 7, 29, true",
            "2024-07-01, 2017-06-30, true, 2024-08-03, 2024, 6, 29, false",
            "2024-08-04, 2017-06-30, true, 2025-08-22, 2025, 7, 29, true",
            "2025-01-01, 2023-12-30, false, 2025-01-28, 2024, 12, 29, false",
            "2024-02-10, 2023-12-30, false, 2025-01-28, 2024, 12, 29, false"
    })
    void usesTheLastDayOfTheActualTargetMonthAndRestoresThirtyInLargeMonths(
            LocalDate today, String storedDate, boolean leapMonth, LocalDate expected,
            int expectedYear, int expectedMonth, int expectedDay, boolean expectedLeapMonth) {
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(today);
            AbstractDateFactory.DateBO result = new LunarCalendarDateFactory().calculateNextDate(storedDate, leapMonth);
            assertEquals(expected, result.solarDate());
            assertEquals(expectedYear, result.lunarDate().getChineseYear());
            assertEquals(expectedMonth, result.lunarDate().getMonth());
            assertEquals(expectedDay, result.lunarDate().getDay());
            assertEquals(expectedLeapMonth, result.lunarDate().isLeapMonth());
        }
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
