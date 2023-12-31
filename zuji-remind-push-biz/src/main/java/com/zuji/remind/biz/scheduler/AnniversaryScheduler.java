package com.zuji.remind.biz.scheduler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.zuji.remind.biz.entity.MemorialDayTask;
import com.zuji.remind.biz.enums.EventTypeEnum;
import com.zuji.remind.biz.model.bo.MemorialDayTaskBO;
import com.zuji.remind.biz.service.db.MemorialDayTaskService;
import com.zuji.remind.biz.service.factory.AbstractEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 纪念日定时任务.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-11 22:22
 **/
@Slf4j
@Component
public class AnniversaryScheduler {
    private final MemorialDayTaskService memorialDayTaskService;
    private final Map<String, AbstractEventFactory> abstractEventFactoryMap;

    public AnniversaryScheduler(MemorialDayTaskService memorialDayTaskService, Map<String, AbstractEventFactory> abstractEventFactoryMap) {
        this.memorialDayTaskService = memorialDayTaskService;
        this.abstractEventFactoryMap = abstractEventFactoryMap;
    }

    @Async("commonThreadPoolExecutor")
    @Scheduled(cron = "0 0 9 * * ?")
    // @Scheduled(cron = "0 0/1 * * * ?")
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
        log.info("当前处理数据: task={}", JSONUtil.toJsonStr(task));
        int statusRemind = task.getStatusRemind();
        if (statusRemind == 0) {
            log.info("当前时间无需提醒,data={}", JSONUtil.toJsonStr(task));
            return;
        }
        MemorialDayTaskBO taskBO = MemorialDayTaskBO.from(task);
        AbstractEventFactory abstractEventFactory = abstractEventFactoryMap.get(EventTypeEnum.getByCode(task.getEventType()).getFactoryName());
        abstractEventFactory.dealWithData(taskBO);
    }
}
