package com.zuji.remind.biz.scheduler;

import com.zuji.remind.biz.component.message.MessageNotifyComponent;
import com.zuji.remind.biz.config.ExecutorConfig;
import com.zuji.remind.biz.repository.MemorialDayTaskRepository;
import com.zuji.remind.biz.repository.MsgPushTaskRepository;
import com.zuji.remind.biz.repository.MsgPushWayRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import static com.zuji.remind.biz.enums.TaskStatusEnum.PUSH_MSG_SCHEDULER_STATUS_LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SchedulingConfigurationTest {

    @Test
    void registersAndInvokesAllScheduledTasks() {
        TaskScheduler taskScheduler = mock(TaskScheduler.class);
        doReturn(mock(ScheduledFuture.class)).when(taskScheduler).schedule(any(Runnable.class), any(Trigger.class));
        MemorialDayTaskRepository memorialRepository = mock(MemorialDayTaskRepository.class);
        MsgPushTaskRepository pushRepository = mock(MsgPushTaskRepository.class);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(ExecutorConfig.class);
            context.registerBean(TaskScheduler.class, () -> taskScheduler);
            context.registerBean(AnniversaryScheduler.class,
                    () -> new AnniversaryScheduler(memorialRepository, Map.of()));
            context.registerBean(PushMessageScheduler.class,
                    () -> new PushMessageScheduler(pushRepository, mock(MsgPushWayRepository.class),
                            mock(MessageNotifyComponent.class)));
            context.registerBean(ClearMessageScheduler.class, () -> new ClearMessageScheduler(pushRepository));
            context.refresh();

            ArgumentCaptor<Runnable> callbacks = ArgumentCaptor.forClass(Runnable.class);
            ArgumentCaptor<Trigger> triggers = ArgumentCaptor.forClass(Trigger.class);
            verify(taskScheduler, times(3)).schedule(callbacks.capture(), triggers.capture());

            Map<String, Runnable> schedules = new HashMap<>();
            for (int i = 0; i < callbacks.getAllValues().size(); i++) {
                CronTrigger trigger = assertInstanceOf(CronTrigger.class, triggers.getAllValues().get(i));
                schedules.put(trigger.getExpression(), callbacks.getAllValues().get(i));
            }
            assertEquals(Set.of("0 0 9 * * ?", "0 */5 * * * ?", "0 0 21 * * ?"), schedules.keySet());

            schedules.get("0 0 9 * * ?").run();
            verify(memorialRepository).listAll();
            schedules.get("0 */5 * * * ?").run();
            verify(pushRepository).listBatchByStatus(PUSH_MSG_SCHEDULER_STATUS_LIST, null, 20L);
            schedules.get("0 0 21 * * ?").run();
            verify(pushRepository).listBatchByMsgIndex(anyInt(), eq(0L), eq(50L));
        }
    }
}
