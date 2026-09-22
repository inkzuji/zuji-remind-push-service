package com.zuji.remind.biz.scheduler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zuji.remind.biz.component.message.AbstractMessageNotifyFactory;
import com.zuji.remind.biz.component.message.MessageNotifyComponent;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.enums.TaskStatusEnum;
import com.zuji.remind.biz.model.bo.MailBO;
import com.zuji.remind.biz.model.bo.MsgPushTaskBO;
import com.zuji.remind.biz.model.bo.MsgPushWayBO;
import com.zuji.remind.biz.model.bo.SendMessageBO;
import com.zuji.remind.biz.repository.MsgPushTaskRepository;
import com.zuji.remind.biz.repository.MsgPushWayRepository;
import com.zuji.remind.common.api.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.zuji.remind.biz.enums.TaskStatusEnum.PUSH_MSG_SCHEDULER_STATUS_LIST;

/**
 * 推送消息定时任务.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-21 13:51
 **/
@Slf4j
@Component
public class PushMessageScheduler {
    private final MsgPushTaskRepository msgPushTaskRepository;
    private final MsgPushWayRepository msgPushWayRepository;
    private final MessageNotifyComponent messageNotifyComponent;

    public PushMessageScheduler(MsgPushTaskRepository msgPushTaskRepository, MsgPushWayRepository msgPushWayRepository, MessageNotifyComponent messageNotifyComponent) {
        this.msgPushTaskRepository = msgPushTaskRepository;
        this.msgPushWayRepository = msgPushWayRepository;
        this.messageNotifyComponent = messageNotifyComponent;
    }

    /**
     * 推送消息定时任务，每 5 分钟执行，扫描待发送消息并执行推送。
     */
    @Async("commonThreadPoolExecutor")
    @Scheduled(cron = "0 */5 * * * ?")
    public void task() {
        log.info("开始执行推送消息定时任务");
        // 每次定时只获取20条数据
        long limit = 20L;

        List<MsgPushTaskBO> taskBOList = msgPushTaskRepository.listBatchByStatus(PUSH_MSG_SCHEDULER_STATUS_LIST, null, limit);
        if (CollectionUtil.isEmpty(taskBOList)) {
            log.info("未查询到需要推送的消息");
            return;
        }

        List<MsgPushWayBO> pushWayList = msgPushWayRepository.listAll();
        Map<RemindWayEnum, List<MsgPushWayBO>> wayBOMap = pushWayList.stream()
                .collect(Collectors.groupingBy(MsgPushWayBO::getPushType));

        for (MsgPushTaskBO taskBO : taskBOList) {
            try {
                log.info("推送消息内容, data = {}", JSONUtil.toJsonStr(taskBO));
                dealWithData(taskBO, wayBOMap);
            } catch (Exception e) {
                log.error("消息推送失败，errMsg={}", e.getMessage(), e);
                dealWithFailData(taskBO.getId(), JSONUtil.toJsonStr(CommonResult.failed(e.getMessage())), taskBO.getFailNum());
            }
        }
        log.info("推送消息定时任务执行结束");
    }

    /**
     * 处理单条推送任务，匹配推送渠道并发送消息。
     */
    private void dealWithData(MsgPushTaskBO taskBO, Map<RemindWayEnum, List<MsgPushWayBO>> wayBOMap) {
        RemindWayEnum msgType = taskBO.getMsgType();
        List<MsgPushWayBO> wayBOList = wayBOMap.get(msgType);
        if (CollectionUtil.isEmpty(wayBOList)) {
            throw new IllegalStateException("推送消息没有读取到推送配置: " + msgType);
        }
        if (wayBOList.size() > 1) {
            throw new IllegalStateException("推送配置重复: " + msgType);
        }
        MsgPushWayBO.WayBO wayBO = wayBOList.get(0).getPushRequestParam();
        if (Objects.isNull(wayBO)) {
            throw new IllegalStateException("推送消息没有读取到推送配置: " + msgType);
        }

        AbstractMessageNotifyFactory notifyFactory = messageNotifyComponent.getByRemindWay(msgType);
        SendMessageBO messageBO = new SendMessageBO();
        if (msgType == RemindWayEnum.EMAIL) {
            messageBO.setMailBO(JSONUtil.toBean(taskBO.getMsgRequest(), MailBO.class));
        }
        if (msgType == RemindWayEnum.DING_DING) {
            messageBO.setDingDingRequest(JSONUtil.toBean(taskBO.getMsgRequest(), OapiRobotSendRequest.class));
        }
        messageBO.setWayBO(wayBO);
        CommonResult<Void> result = notifyFactory.send(messageBO);
        if (result.isSuccess()) {
            dealWithSuccessData(taskBO.getId(), JSONUtil.toJsonStr(result));
            return;
        }
        dealWithFailData(taskBO.getId(), JSONUtil.toJsonStr(result), taskBO.getFailNum());
    }

    /**
     * 处理推送成功的数据，更新状态为已发送。
     */
    private void dealWithSuccessData(Long id, String msgResponse) {
        msgPushTaskRepository.updateStatusById(id, TaskStatusEnum.SUCCESS.getCode(), msgResponse, null);
    }

    /**
     * 处理推送失败的数据，累计失败次数，超过 20 次标记为永久失败。
     */
    private void dealWithFailData(Long id, String msgResponse, Integer failNum) {
        TaskStatusEnum statusEnum = TaskStatusEnum.FAIL_TRIED_AGAIN_SEND;
        failNum = failNum + 1;
        if (failNum > 20) {
            statusEnum = TaskStatusEnum.FAIL_SEND;
        }
        msgPushTaskRepository.updateStatusById(id, statusEnum.getCode(), msgResponse, failNum);
    }
}
