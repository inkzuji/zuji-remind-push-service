package com.zuji.remind.biz.service.factory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONUtil;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.google.common.collect.Lists;
import com.zuji.remind.biz.component.datecal.AbstractDateFactory;
import com.zuji.remind.biz.component.notify.AbstractNotifyFactory;
import com.zuji.remind.biz.entity.MsgPushTask;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.enums.TaskStatusEnum;
import com.zuji.remind.biz.model.bo.AggreNotifyBO;
import com.zuji.remind.biz.model.bo.EventContextBO;
import com.zuji.remind.biz.model.bo.MailBO;
import com.zuji.remind.biz.model.bo.MemorialDayTaskBO;
import com.zuji.remind.biz.service.db.MsgPushTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<AggreNotifyBO> dealWithData(MemorialDayTaskBO bo) {
        EventContextBO contextBO = EventContextBO.init(bo);
        contextBO.setDateFactory(AbstractDateFactory.getInstance(bo.getDateType()));
        contextBO.setNotifyFactory(AbstractNotifyFactory.getInstance(bo.getEventType()));
        calculateDate(contextBO);
        calculateNotify(contextBO);
        return getSendMsg(contextBO);
    }

    public void saveMessage(Map<RemindWayEnum, List<AggreNotifyBO>> notifyMap) {
        if (CollUtil.isEmpty(notifyMap)) {
            return;
        }

        int msgIndex = Integer.parseInt(LocalDateTime.now().format(DatePattern.PURE_DATE_FORMATTER));
        log.info("新增推送消息任务索引: msgIndex={}", msgIndex);
        List<MsgPushTask> taskList = Lists.newLinkedList();
        notifyMap.forEach((remindWay, notifyList) -> {
            MsgPushTask task = new MsgPushTask();
            task.setMsgType(remindWay.getCode());
            task.setStatus(TaskStatusEnum.WAITING_SEND.getCode());
            task.setFailNum(0);
            task.setMsgIndex(msgIndex);
            switch (remindWay) {
                case EMAIL:
                    task.setMsgRequest(markEmailBody(notifyList));
                    break;
                case DING_DING:
                    task.setMsgRequest(markDingDingBody(notifyList));
                    break;
                case WECHAT:
                default:
                    throw new RuntimeException("暂不支持[" + remindWay + "]方式");
            }
            taskList.add(task);
        });
        msgPushTaskService.addBatch(taskList);
    }

    /**
     * 拼接通知内容。
     */
    private List<AggreNotifyBO> getSendMsg(EventContextBO contextBO) {
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();
        if (BooleanUtils.isNotTrue(originalDB.getStatusRemind())) {
            return Collections.emptyList();
        }
        if (BooleanUtils.isNotTrue(calculateResultBO.getIsNotify())) {
            return Collections.emptyList();
        }
        List<AggreNotifyBO> list = Lists.newArrayListWithCapacity(3);
        for (RemindWayEnum remindWay : originalDB.getRemindWays()) {
            AggreNotifyBO notifyBO = new AggreNotifyBO();
            notifyBO.setEventTypeEnum(originalDB.getEventType());
            notifyBO.setRemindWayEnum(remindWay);
            switch (remindWay) {
                case EMAIL:
                    notifyBO.setMsg(getEmailMsgContent(contextBO));
                    break;
                case DING_DING:
                    notifyBO.setMsg(getDingDingMsgContent(contextBO));
                    break;
                case WECHAT:
                default:
                    throw new RuntimeException("暂不支持[" + remindWay + "]方式");
            }
            list.add(notifyBO);
        }
        return list;
    }

    private String markEmailBody(List<AggreNotifyBO> notifyList) {
        String content = notifyList.stream().map(AggreNotifyBO::getMsg).collect(Collectors.joining("  <br/>  "));
        MailBO emailBO = getEmailBO(String.format("<html>%s</html>", content));
        return JSONUtil.toJsonStr(emailBO);
    }

    private String markDingDingBody(List<AggreNotifyBO> notifyList) {
        String content = notifyList.stream().map(AggreNotifyBO::getMsg).collect(Collectors.joining("  \n  "));
        OapiRobotSendRequest dingDingMessageBody = getDingDingMessageBody(content);
        return JSONUtil.toJsonStr(dingDingMessageBody);
    }

    abstract void calculateDate(EventContextBO contextBO);

    abstract void calculateNotify(EventContextBO contextBO);

    abstract MailBO getEmailBO(String body);

    abstract OapiRobotSendRequest getDingDingMessageBody(String body);

    /**
     * 获取邮件内容。
     */
    abstract String getEmailMsgContent(EventContextBO contextBO);

    /**
     * 获取钉钉消息内容
     */
    abstract String getDingDingMsgContent(EventContextBO contextBO);
}
