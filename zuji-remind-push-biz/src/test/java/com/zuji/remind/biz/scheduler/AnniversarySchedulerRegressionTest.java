package com.zuji.remind.biz.scheduler;

import com.zuji.remind.biz.dao.entity.MemorialDayTask;
import com.zuji.remind.biz.enums.EventTypeEnum;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.model.bo.AggreNotifyBO;
import com.zuji.remind.biz.model.bo.MemorialDayTaskBO;
import com.zuji.remind.biz.repository.MemorialDayTaskRepository;
import com.zuji.remind.biz.service.factory.AbstractEventFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnniversarySchedulerRegressionTest {

    @Test
    void invalidTaskDoesNotDiscardEarlierOrLaterNotifications() {
        MemorialDayTaskRepository repository = mock(MemorialDayTaskRepository.class);
        AbstractEventFactory factory = mock(AbstractEventFactory.class);
        when(repository.listAll()).thenReturn(List.of(task(1L), task(2L), task(3L)));
        AggreNotifyBO first = notification("first");
        AggreNotifyBO last = notification("last");
        when(factory.dealWithData(any(MemorialDayTaskBO.class))).thenAnswer(invocation -> {
            MemorialDayTaskBO task = invocation.getArgument(0);
            if (task.getId() == 2L) {
                throw new NumberFormatException("invalid remindTimes");
            }
            return List.of(task.getId() == 1L ? first : last);
        });
        AnniversaryScheduler scheduler = new AnniversaryScheduler(repository,
                Map.of(EventTypeEnum.ANNIVERSARY.getFactoryName(), factory));

        assertDoesNotThrow(scheduler::task);

        verify(factory).saveMessage(Map.of(RemindWayEnum.EMAIL, List.of(first, last)));
    }

    private MemorialDayTask task(Long id) {
        MemorialDayTask task = new MemorialDayTask();
        task.setId(id);
        task.setEventType(EventTypeEnum.ANNIVERSARY.getCode());
        task.setDateType(1);
        task.setIsLeapMonth(0);
        task.setStatusRemind(1);
        task.setRemindWay("1");
        return task;
    }

    private AggreNotifyBO notification(String message) {
        AggreNotifyBO notification = new AggreNotifyBO();
        notification.setEventTypeEnum(EventTypeEnum.ANNIVERSARY);
        notification.setRemindWayEnum(RemindWayEnum.EMAIL);
        notification.setMsg(message);
        return notification;
    }
}
