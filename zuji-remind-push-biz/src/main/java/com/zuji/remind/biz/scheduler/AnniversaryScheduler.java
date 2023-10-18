package com.zuji.remind.biz.scheduler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zuji.remind.biz.client.DingDingPushClient;
import com.zuji.remind.biz.entity.MemorialDayTask;
import com.zuji.remind.biz.service.db.MemorialDayTaskService;
import com.zuji.remind.biz.service.factory.AbstractEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 纪念日定时任务.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-11 22:22
 **/
@Slf4j
@Component
public class AnniversaryScheduler {
    private final MemorialDayTaskService memorialDayTaskService;
    private final DingDingPushClient dingDingPushClient;

    public AnniversaryScheduler(MemorialDayTaskService memorialDayTaskService, DingDingPushClient dingDingPushClient) {
        this.memorialDayTaskService = memorialDayTaskService;
        this.dingDingPushClient = dingDingPushClient;
    }

    @Async("commonThreadPoolExecutor")
    @Scheduled(cron = "0 0 9 * * ?")
    public void task() {
        log.info("开始纪念日定时任务");
        List<MemorialDayTask> memorialDayTaskList = memorialDayTaskService.listAll();
        if (CollectionUtil.isEmpty(memorialDayTaskList)) {
            log.info("暂无数据");
            return;
        }
        for (MemorialDayTask dayTask : memorialDayTaskList) {
            this.dealWithData(dayTask);
        }
        log.info("纪念日定时任务执行完成");
    }

    private void dealWithData(MemorialDayTask task) {
        log.info("开始处理数据: task={}", JSONUtil.toJsonStr(task));
        int statusRemind = task.getStatusRemind();
        if (statusRemind == 0) {
            log.info("当前时间无需提醒,data={}", JSONUtil.toJsonStr(task));
            return;
        }
        String remindTimes = task.getRemindTimes();

        AbstractEventFactory abstractEventFactory = AbstractEventFactory.getInstance(task.getEventType(), task.getDateType(), task.getMemorialDate(), task.getIsLeapMonth(), remindTimes);
        boolean notifyResult = abstractEventFactory.analyzeNotify();
        log.info("处理结果返回值, notifyResult={}", notifyResult);
        if (!notifyResult) {
            log.info("未到推送通知时间, result = {}", abstractEventFactory);
            return;
        }
        OapiRobotSendRequest sendRequest = abstractEventFactory.getDingDingMessageBody(task.getName(), task.getTaskDesc());
        log.info("机器人消息: msg={}", JSONUtil.toJsonStr(sendRequest));
        dingDingPushClient.send(sendRequest);
    }
}
