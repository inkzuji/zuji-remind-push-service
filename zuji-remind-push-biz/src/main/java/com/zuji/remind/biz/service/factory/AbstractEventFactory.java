package com.zuji.remind.biz.service.factory;

import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONUtil;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.google.common.collect.Lists;
import com.zuji.remind.biz.component.datecal.AbstractDateFactory;
import com.zuji.remind.biz.component.notify.AbstractNotifyFactory;
import com.zuji.remind.biz.entity.MsgPushTask;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.enums.TaskStatusEnum;
import com.zuji.remind.biz.model.bo.EventContextBO;
import com.zuji.remind.biz.model.bo.MailBO;
import com.zuji.remind.biz.model.bo.MemorialDayTaskBO;
import com.zuji.remind.biz.service.db.MsgPushTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 抽象类型.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-11 22:58
 **/
@Slf4j
public abstract class AbstractEventFactory {

    private MsgPushTaskService msgPushTaskService;

    @Autowired
    public void setMsgPushTaskService(MsgPushTaskService msgPushTaskService) {
        this.msgPushTaskService = msgPushTaskService;
    }

    public void dealWithData(MemorialDayTaskBO bo) {
        EventContextBO contextBO = EventContextBO.init(bo);
        contextBO.setDateFactory(AbstractDateFactory.getInstance(bo.getDateType()));
        contextBO.setNotifyFactory(AbstractNotifyFactory.getInstance(bo.getEventType()));
        calculateDate(contextBO);
        calculateNotify(contextBO);
        sendMessage(contextBO);
    }

    private void sendMessage(EventContextBO contextBO) {
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();
        if (BooleanUtils.isNotTrue(originalDB.getStatusRemind())) {
            return;
        }
        if (BooleanUtils.isNotTrue(calculateResultBO.getIsNotify())) {
            return;
        }
        log.info("我现在可以提醒了");

        int msgIndex = Integer.parseInt(LocalDateTime.now().format(DatePattern.PURE_DATE_FORMATTER));
        log.info("新增推送消息任务索引: msgIndex={}", msgIndex);
        List<MsgPushTask> taskList = Lists.newLinkedList();
        for (RemindWayEnum remindWay : originalDB.getRemindWays()) {
            MsgPushTask task = new MsgPushTask();
            task.setMsgType(remindWay.getCode());
            task.setStatus(TaskStatusEnum.WAITING_SEND.getCode());
            task.setFailNum(0);
            task.setMsgIndex(msgIndex);
            switch (remindWay) {
                case EMAIL:
                    task.setMsgRequest(JSONUtil.toJsonStr(getEmailBO(contextBO)));
                    break;
                case DING_DING:
                    task.setMsgRequest(JSONUtil.toJsonStr(getDingDingMessageBody(contextBO)));
                    break;
                case WECHAT:
                default:
                    throw new RuntimeException("暂不支持[" + remindWay + "]方式");
            }
            taskList.add(task);
        }
        msgPushTaskService.addBatch(taskList);
    }

    abstract void calculateDate(EventContextBO contextBO);

    abstract void calculateNotify(EventContextBO contextBO);

    abstract MailBO getEmailBO(EventContextBO contextBO);

    abstract OapiRobotSendRequest getDingDingMessageBody(EventContextBO contextBO);
}
