package com.zuji.remind.biz.scheduler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONUtil;
import com.zuji.remind.biz.dao.entity.MsgPushTask;
import com.zuji.remind.biz.repository.MsgPushTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 清理消息定时任务.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-21 15:09
 **/
@Slf4j
@Component
public class ClearMessageScheduler {
    private final MsgPushTaskRepository msgPushTaskRepository;

    public ClearMessageScheduler(MsgPushTaskRepository msgPushTaskRepository) {
        this.msgPushTaskRepository = msgPushTaskRepository;
    }

    /**
     * 清理消息定时任务，每天 21:00 执行，删除 7 天前的推送消息。
     */
    @Async("commonThreadPoolExecutor")
    @Scheduled(cron = "0 0 21 * * ?")
    public void task() {
        log.info("开始执行清除推送消息定时任务");

        // 定义删除索引，删除7天前的数据
        int msgIndex = Integer.parseInt(LocalDateTime.now().minusDays(7L).format(DatePattern.PURE_DATE_FORMATTER));
        long limit = 50L;
        long id = 0;
        while (true) {
            List<MsgPushTask> taskList = msgPushTaskRepository.listBatchByMsgIndex(msgIndex, id, limit);
            if (CollectionUtil.isEmpty(taskList)) {
                log.info("未查询到清除消息内容");
                break;
            }
            id = taskList.get(taskList.size() - 1).getId();
            List<Long> delIds = taskList.stream()
                    .map(MsgPushTask::getId)
                    .collect(Collectors.toList());
            log.info("当前清理数据: ids={}", JSONUtil.toJsonStr(delIds));
            msgPushTaskRepository.deleteBatch(delIds);
        }
        log.info("清除推送消息定时任务执行完成");
    }
}
