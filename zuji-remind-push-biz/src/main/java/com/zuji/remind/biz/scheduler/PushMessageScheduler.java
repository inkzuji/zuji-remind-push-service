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
import com.zuji.remind.biz.service.db.MsgPushTaskService;
import com.zuji.remind.biz.service.db.MsgPushWayService;
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
    private final MsgPushTaskService msgPushTaskService;
    private final MsgPushWayService msgPushWayService;
    private final MessageNotifyComponent messageNotifyComponent;

    public PushMessageScheduler(MsgPushTaskService msgPushTaskService, MsgPushWayService msgPushWayService, MessageNotifyComponent messageNotifyComponent) {
        this.msgPushTaskService = msgPushTaskService;
        this.msgPushWayService = msgPushWayService;
        this.messageNotifyComponent = messageNotifyComponent;
    }

    @Async("commonThreadPoolExecutor")
    @Scheduled(cron = "0 */5 * * * ?")
    public void task() {
        log.info("开始执行推送消息定时任务");
        // 每次定时只获取20条数据
        long limit = 20L;

        List<MsgPushTaskBO> taskBOList = msgPushTaskService.listBatchByStatus(PUSH_MSG_SCHEDULER_STATUS_LIST, null, limit);
        if (CollectionUtil.isEmpty(taskBOList)) {
            log.info("未查询到需要推送的消息");
            return;
        }

        List<MsgPushWayBO> pushWayList = msgPushWayService.listAll();
        Map<RemindWayEnum, MsgPushWayBO.WayBO> wayBOMap = pushWayList.stream()
                .collect(Collectors.toMap(MsgPushWayBO::getPushType, MsgPushWayBO::getPushRequestParam));

        for (MsgPushTaskBO taskBO : taskBOList) {
            try {
                dealWithData(taskBO, wayBOMap);
            } catch (Exception e) {
                log.error("消息推送失败，errMsg={}", e.getMessage(), e);
                dealWithFailData(taskBO.getId(), JSONUtil.toJsonStr(CommonResult.failed(e.getMessage())), taskBO.getFailNum());
            }
        }
        log.info("推送消息定时任务执行结束");
    }

    private void dealWithData(MsgPushTaskBO taskBO, Map<RemindWayEnum, MsgPushWayBO.WayBO> wayBOMap) {
        RemindWayEnum msgType = taskBO.getMsgType();
        MsgPushWayBO.WayBO wayBO = wayBOMap.get(msgType);
        if (Objects.isNull(wayBO)) {
            log.error("推送消息没有读取到推送配置, msg={}", JSONUtil.toJsonStr(msgType));
            return;
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

    private void dealWithSuccessData(Long id, String msgResponse) {
        msgPushTaskService.updateStatusById(id, TaskStatusEnum.SUCCESS.getCode(), msgResponse, null);
    }

    private void dealWithFailData(Long id, String msgResponse, Integer failNum) {
        TaskStatusEnum statusEnum = TaskStatusEnum.FAIL_TRIED_AGAIN_SEND;
        failNum = failNum + 1;
        if (failNum > 20) {
            statusEnum = TaskStatusEnum.FAIL_SEND;
        }
        msgPushTaskService.updateStatusById(id, statusEnum.getCode(), msgResponse, failNum);
    }
}
