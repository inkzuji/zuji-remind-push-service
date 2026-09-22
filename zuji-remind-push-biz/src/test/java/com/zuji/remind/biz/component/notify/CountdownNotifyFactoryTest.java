package com.zuji.remind.biz.component.notify;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

class CountdownNotifyFactoryTest {

    @Test
    void countsDownToFutureDatesAndStopsAfterTheEvent() {
        LocalDate today = LocalDate.of(2026, 9, 22);
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(today);
            CountdownNotifyFactory factory = new CountdownNotifyFactory();
            assertEquals(new AbstractNotifyFactory.NotifyBO(true, 7L), factory.analyzeIsNotify(today.plusDays(7), "7"));
            assertEquals(new AbstractNotifyFactory.NotifyBO(false, -1L), factory.analyzeIsNotify(today.minusDays(7), "7"));
            assertEquals(new AbstractNotifyFactory.NotifyBO(true, 0L), factory.analyzeIsNotify(today, "7"));
            assertEquals(new AbstractNotifyFactory.NotifyBO(false, 8L), factory.analyzeIsNotify(today.plusDays(8), "7"));
        }
    }

    @Test
    void weeklyReminderOnlyAppliesBeforeTheEvent() {
        LocalDate monday = LocalDate.of(2026, 9, 21);
        try (MockedStatic<LocalDate> dates = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            dates.when(LocalDate::now).thenReturn(monday);
            CountdownNotifyFactory factory = new CountdownNotifyFactory();
            assertEquals(new AbstractNotifyFactory.NotifyBO(true, 8L), factory.analyzeIsNotify(monday.plusDays(8), "7"));
            assertEquals(new AbstractNotifyFactory.NotifyBO(false, -1L), factory.analyzeIsNotify(monday.minusDays(8), "7"));
        }
    }
}
