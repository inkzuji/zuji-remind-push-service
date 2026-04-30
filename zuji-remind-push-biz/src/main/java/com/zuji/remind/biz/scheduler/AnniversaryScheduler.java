package com.zuji.remind.biz.scheduler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.zuji.remind.biz.dao.entity.MemorialDayTask;
import com.zuji.remind.biz.enums.EnableStatusEnum;
import com.zuji.remind.biz.enums.EventTypeEnum;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.model.bo.AggreNotifyBO;
import com.zuji.remind.biz.model.bo.MemorialDayTaskBO;
import com.zuji.remind.biz.repository.MemorialDayTaskRepository;
import com.zuji.remind.biz.service.factory.AbstractEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 纪念日定时任务.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-11 22:22
 **/
@Slf4j
@Component
public class AnniversaryScheduler {
    private final MemorialDayTaskRepository memorialDayTaskRepository;
    private final Map<String, AbstractEventFactory> abstractEventFactoryMap;

    public AnniversaryScheduler(MemorialDayTaskRepository memorialDayTaskRepository, Map<String, AbstractEventFactory> abstractEventFactoryMap) {
        this.memorialDayTaskRepository = memorialDayTaskRepository;
        this.abstractEventFactoryMap = abstractEventFactoryMap;
    }

    /**
     * 纪念日定时任务，每天 09:00 执行，扫描所有纪念日事件并生成推送消息。
     */
    @Async("commonThreadPoolExecutor")
    @Scheduled(cron = "0 0 9 * * ?")
    // @Scheduled(cron = "0 0/1 * * * ?")
    public void task() {
        log.info("开始纪念日定时任务");
        List<MemorialDayTask> memorialDayTaskList = memorialDayTaskRepository.listAll();
        if (CollectionUtil.isEmpty(memorialDayTaskList)) {
            log.info("暂无数据");
            return;
        }
        List<AggreNotifyBO> notifyList = new ArrayList<>();
        for (MemorialDayTask dayTask : memorialDayTaskList) {
            this.dealWithData(dayTask, notifyList);
        }
        this.saveSendMsg(notifyList);
        log.info("纪念日定时任务执行完成");
    }

    /**
     * 处理单条纪念日任务，根据事件类型匹配对应工厂并计算通知消息。
     */
    private void dealWithData(MemorialDayTask task, List<AggreNotifyBO> notifyList) {
        log.info("当前处理数据: task={}", JSONUtil.toJsonStr(task));
        EnableStatusEnum statusRemind = EnableStatusEnum.getByCode(task.getStatusRemind());
        if (EnableStatusEnum.DISABLE == statusRemind) {
            log.info("当前事件无需提醒,data={}", JSONUtil.toJsonStr(task));
            return;
        }
        MemorialDayTaskBO taskBO = MemorialDayTaskBO.from(task);
        AbstractEventFactory abstractEventFactory = abstractEventFactoryMap.get(EventTypeEnum.getByCode(task.getEventType()).getFactoryName());
        List<AggreNotifyBO> aggreNotifyBOList = abstractEventFactory.dealWithData(taskBO);
        notifyList.addAll(aggreNotifyBOList);
    }

    /**
     * 保存消息通知。
     *
     * @param notifyList 通知内容
     */
    private void saveSendMsg(List<AggreNotifyBO> notifyList) {
        if (CollUtil.isEmpty(notifyList)) {
            log.info("暂无需要推送消息");
            return;
        }
        log.info("保存推送消息, context = {}", JSONUtil.toJsonStr(notifyList));
        Map<EventTypeEnum, Map<RemindWayEnum, List<AggreNotifyBO>>> map = notifyList.stream()
                .collect(Collectors.groupingBy(AggreNotifyBO::getEventTypeEnum,
                        Collectors.groupingBy(AggreNotifyBO::getRemindWayEnum)));

        map.forEach((eventType, notifyMap) -> {
            AbstractEventFactory abstractEventFactory = abstractEventFactoryMap.get(eventType.getFactoryName());
            abstractEventFactory.saveMessage(notifyMap);
        });
    }
}
