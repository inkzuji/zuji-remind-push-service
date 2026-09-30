package com.zuji.remind.biz.scheduler;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.zuji.remind.biz.component.message.AbstractMessageNotifyFactory;
import com.zuji.remind.biz.component.message.MessageNotifyComponent;
import com.zuji.remind.biz.dao.entity.MsgPushWay;
import com.zuji.remind.biz.dao.mapper.MsgPushWayMapper;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.enums.TaskStatusEnum;
import com.zuji.remind.biz.model.bo.MsgPushTaskBO;
import com.zuji.remind.biz.model.bo.MsgPushWayBO;
import com.zuji.remind.biz.model.bo.SendMessageBO;
import com.zuji.remind.biz.repository.MsgPushTaskRepository;
import com.zuji.remind.biz.repository.MsgPushWayRepository;
import com.zuji.remind.biz.repository.impl.MsgPushWayRepositoryImpl;
import com.zuji.remind.common.api.CommonResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Test
    void malformedChannelCountsAsFailureAndOtherChannelStillSends() {
        when(taskRepository.listBatchByStatus(PUSH_MSG_SCHEDULER_STATUS_LIST, null, 20L))
                .thenReturn(List.of(task(1L, RemindWayEnum.DING_DING, 0), task(2L, RemindWayEnum.EMAIL, 0)));
        useStoredWays(storedWay(RemindWayEnum.DING_DING, "{invalid"), storedWay(RemindWayEnum.EMAIL, "{}"));
        AbstractMessageNotifyFactory emailFactory = mock(AbstractMessageNotifyFactory.class);
        when(notifyComponent.getByRemindWay(RemindWayEnum.EMAIL)).thenReturn(emailFactory);
        when(emailFactory.send(any(SendMessageBO.class))).thenReturn(CommonResult.success());

        scheduler.task();

        verify(taskRepository).updateStatusById(eq(1L), eq(TaskStatusEnum.FAIL_TRIED_AGAIN_SEND.getCode()),
                contains("推送配置解析失败"), eq(1));
        verify(notifyComponent, never()).getByRemindWay(RemindWayEnum.DING_DING);
        verify(emailFactory).send(any(SendMessageBO.class));
        verify(taskRepository).updateStatusById(eq(2L), eq(TaskStatusEnum.SUCCESS.getCode()), anyString(), isNull());
    }

    @Test
    void malformedUnusedChannelDoesNotBlockValidChannel() {
        when(taskRepository.listBatchByStatus(PUSH_MSG_SCHEDULER_STATUS_LIST, null, 20L))
                .thenReturn(List.of(task(2L, RemindWayEnum.DING_DING, 0)));
        useStoredWays(storedWay(RemindWayEnum.EMAIL, "{invalid"), storedWay(RemindWayEnum.DING_DING, "{}"));
        AbstractMessageNotifyFactory dingDingFactory = successfulDingDingFactory();

        scheduler.task();

        verify(dingDingFactory).send(any(SendMessageBO.class));
        verify(taskRepository).updateStatusById(eq(2L), eq(TaskStatusEnum.SUCCESS.getCode()), anyString(), isNull());
    }

    @Test
    void malformedChannelEventuallyLeavesRetryQueue() {
        when(taskRepository.listBatchByStatus(PUSH_MSG_SCHEDULER_STATUS_LIST, null, 20L))
                .thenReturn(List.of(task(1L, RemindWayEnum.EMAIL, 20)));
        useStoredWays(storedWay(RemindWayEnum.EMAIL, "{invalid"));

        scheduler.task();

        verify(taskRepository).updateStatusById(eq(1L), eq(TaskStatusEnum.FAIL_SEND.getCode()),
                contains("推送配置解析失败"), eq(21));
        verifyNoInteractions(notifyComponent);
    }

    @Test
    void validAndMalformedConfigurationsStillCountAsDuplicateChannel() {
        when(taskRepository.listBatchByStatus(PUSH_MSG_SCHEDULER_STATUS_LIST, null, 20L))
                .thenReturn(List.of(task(1L, RemindWayEnum.EMAIL, 0)));
        useStoredWays(storedWay(RemindWayEnum.EMAIL, "{}"), storedWay(RemindWayEnum.EMAIL, "{invalid"));

        scheduler.task();

        verify(taskRepository).updateStatusById(eq(1L), eq(TaskStatusEnum.FAIL_TRIED_AGAIN_SEND.getCode()),
                contains("推送配置重复"), eq(1));
        verifyNoInteractions(notifyComponent);
    }

    private void useStoredWays(MsgPushWay... ways) {
        MsgPushWayMapper mapper = mock(MsgPushWayMapper.class);
        when(mapper.selectList(ArgumentMatchers.<Wrapper<MsgPushWay>>any())).thenReturn(List.of(ways));
        MsgPushWayRepositoryImpl repository = new MsgPushWayRepositoryImpl();
        ReflectionTestUtils.setField(repository, "baseMapper", mapper);
        scheduler = new PushMessageScheduler(taskRepository, repository, notifyComponent);
    }

    private MsgPushWay storedWay(RemindWayEnum type, String context) {
        MsgPushWay way = new MsgPushWay();
        way.setPushType(type.getCode());
        way.setPushContext(context);
        return way;
    }

    private AbstractMessageNotifyFactory successfulDingDingFactory() {
        AbstractMessageNotifyFactory factory = mock(AbstractMessageNotifyFactory.class);
        when(notifyComponent.getByRemindWay(RemindWayEnum.DING_DING)).thenReturn(factory);
        when(factory.send(any(SendMessageBO.class))).thenReturn(CommonResult.success());
        return factory;
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
