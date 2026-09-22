package com.zuji.remind.biz.scheduler;

import com.zuji.remind.biz.component.message.AbstractMessageNotifyFactory;
import com.zuji.remind.biz.component.message.MessageNotifyComponent;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.enums.TaskStatusEnum;
import com.zuji.remind.biz.model.bo.MsgPushTaskBO;
import com.zuji.remind.biz.model.bo.MsgPushWayBO;
import com.zuji.remind.biz.model.bo.SendMessageBO;
import com.zuji.remind.biz.repository.MsgPushTaskRepository;
import com.zuji.remind.biz.repository.MsgPushWayRepository;
import com.zuji.remind.common.api.CommonResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.zuji.remind.biz.enums.TaskStatusEnum.PUSH_MSG_SCHEDULER_STATUS_LIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PushMessageSchedulerRegressionTest {

    private MsgPushTaskRepository taskRepository;
    private MsgPushWayRepository wayRepository;
    private MessageNotifyComponent notifyComponent;
    private PushMessageScheduler scheduler;

    @BeforeEach
    void setUp() {
        taskRepository = mock(MsgPushTaskRepository.class);
        wayRepository = mock(MsgPushWayRepository.class);
        notifyComponent = mock(MessageNotifyComponent.class);
        scheduler = new PushMessageScheduler(taskRepository, wayRepository, notifyComponent);
    }

    @Test
    void missingChannelCountsAsRetryableFailure() {
        when(taskRepository.listBatchByStatus(PUSH_MSG_SCHEDULER_STATUS_LIST, null, 20L))
                .thenReturn(List.of(task(1L, RemindWayEnum.EMAIL, 0)));
        when(wayRepository.listAll()).thenReturn(List.of());

        scheduler.task();

        verify(taskRepository).updateStatusById(eq(1L), eq(TaskStatusEnum.FAIL_TRIED_AGAIN_SEND.getCode()),
                contains("没有读取到推送配置"), eq(1));
        verifyNoInteractions(notifyComponent);
    }

    @Test
    void missingChannelEventuallyLeavesRetryQueue() {
        when(taskRepository.listBatchByStatus(PUSH_MSG_SCHEDULER_STATUS_LIST, null, 20L))
                .thenReturn(List.of(task(1L, RemindWayEnum.EMAIL, 20)));
        when(wayRepository.listAll()).thenReturn(List.of());

        scheduler.task();

        verify(taskRepository).updateStatusById(eq(1L), eq(TaskStatusEnum.FAIL_SEND.getCode()),
                contains("没有读取到推送配置"), eq(21));
        verifyNoInteractions(notifyComponent);
    }

    @Test
    void duplicateChannelFailsItsTaskAndOtherChannelStillSends() {
        when(taskRepository.listBatchByStatus(PUSH_MSG_SCHEDULER_STATUS_LIST, null, 20L))
                .thenReturn(List.of(task(1L, RemindWayEnum.EMAIL, 0), task(2L, RemindWayEnum.DING_DING, 0)));
        MsgPushWayBO.DingDingBO dingDingWay = new MsgPushWayBO.DingDingBO();
        when(wayRepository.listAll()).thenReturn(List.of(
                way(RemindWayEnum.EMAIL, new MsgPushWayBO.EmailWayBO()),
                way(RemindWayEnum.EMAIL, new MsgPushWayBO.EmailWayBO()),
                way(RemindWayEnum.DING_DING, dingDingWay)));
        AbstractMessageNotifyFactory dingDingFactory = mock(AbstractMessageNotifyFactory.class);
        when(notifyComponent.getByRemindWay(RemindWayEnum.DING_DING)).thenReturn(dingDingFactory);
        when(dingDingFactory.send(any(SendMessageBO.class))).thenReturn(CommonResult.success());

        scheduler.task();

        verify(taskRepository).updateStatusById(eq(1L), eq(TaskStatusEnum.FAIL_TRIED_AGAIN_SEND.getCode()),
                contains("推送配置重复"), eq(1));
        verify(notifyComponent, never()).getByRemindWay(RemindWayEnum.EMAIL);
        verify(dingDingFactory).send(any(SendMessageBO.class));
        verify(taskRepository).updateStatusById(eq(2L), eq(TaskStatusEnum.SUCCESS.getCode()), anyString(), isNull());
    }

    private MsgPushTaskBO task(Long id, RemindWayEnum type, int failNum) {
        MsgPushTaskBO task = new MsgPushTaskBO();
        task.setId(id);
        task.setMsgType(type);
        task.setMsgRequest("{}");
        task.setFailNum(failNum);
        return task;
    }

    private MsgPushWayBO way(RemindWayEnum type, MsgPushWayBO.WayBO requestParam) {
        MsgPushWayBO way = new MsgPushWayBO();
        way.setPushType(type);
        way.setPushRequestParam(requestParam);
        return way;
    }
}
