package com.zuji.remind.biz.scheduler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.zuji.remind.biz.entity.MemorialDayTask;
import com.zuji.remind.biz.entity.MsgPushWay;
import com.zuji.remind.biz.enums.EventTypeEnum;
import com.zuji.remind.biz.service.db.MemorialDayTaskService;
import com.zuji.remind.biz.service.db.MsgPushWayService;
import com.zuji.remind.biz.service.factory.AbstractEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final MsgPushWayService msgPushWayService;
    private final Map<String, AbstractEventFactory> abstractEventFactoryMap;

    public AnniversaryScheduler(MemorialDayTaskService memorialDayTaskService, MsgPushWayService msgPushWayService, Map<String, AbstractEventFactory> abstractEventFactoryMap) {
        this.memorialDayTaskService = memorialDayTaskService;
        this.msgPushWayService = msgPushWayService;
        this.abstractEventFactoryMap = abstractEventFactoryMap;
    }

    @Async("commonThreadPoolExecutor")
    @Scheduled(cron = "0 0 9/21 * * ?")
    // @Scheduled(cron = "0 0/1 * * * ?")
    public void task() {
        log.info("开始纪念日定时任务");
        List<MemorialDayTask> memorialDayTaskList = memorialDayTaskService.listAll();
        if (CollectionUtil.isEmpty(memorialDayTaskList)) {
            log.info("暂无数据");
            return;
        }
        List<MsgPushWay> msgPushWayList = msgPushWayService.listAll();
        Map<Integer, MsgPushWay> pushWayMap = msgPushWayList.stream()
                .collect(Collectors.toMap(MsgPushWay::getPushType, Function.identity()));
        for (MemorialDayTask dayTask : memorialDayTaskList) {
            this.dealWithData(dayTask, pushWayMap);
        }
        log.info("纪念日定时任务执行完成");
    }

    private void dealWithData(MemorialDayTask task, Map<Integer, MsgPushWay> pushWayMap) {
        log.info("开始处理数据: task={}", JSONUtil.toJsonStr(task));
        int statusRemind = task.getStatusRemind();
        if (statusRemind == 0) {
            log.info("当前时间无需提醒,data={}", JSONUtil.toJsonStr(task));
            return;
        }
        String remindTimes = task.getRemindTimes();

        AbstractEventFactory abstractEventFactory = abstractEventFactoryMap.get(EventTypeEnum.getByCode(task.getEventType()).getFactoryName());
        boolean notifyResult = abstractEventFactory.analyzeNotify(task.getDateType(),
                task.getMemorialDate(),
                task.getIsLeapMonth(),
                remindTimes);
        log.info("处理结果返回值, notifyResult={}", notifyResult);
        if (!notifyResult) {
            log.info("未到推送通知时间, result = {}", abstractEventFactory);
            return;
        }
        abstractEventFactory.sendMsg(pushWayMap, task.getRemindWay(), task.getName(), task.getTaskDesc());
    }
}
