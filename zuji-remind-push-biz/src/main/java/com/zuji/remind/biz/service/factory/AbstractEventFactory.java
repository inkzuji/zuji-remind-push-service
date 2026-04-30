package com.zuji.remind.biz.service.factory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONUtil;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.google.common.collect.Lists;
import com.zuji.remind.biz.component.datecal.AbstractDateFactory;
import com.zuji.remind.biz.component.notify.AbstractNotifyFactory;
import com.zuji.remind.biz.dao.entity.MsgPushTask;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.enums.TaskStatusEnum;
import com.zuji.remind.biz.model.bo.AggreNotifyBO;
import com.zuji.remind.biz.model.bo.EventContextBO;
import com.zuji.remind.biz.model.bo.MailBO;
import com.zuji.remind.biz.model.bo.MemorialDayTaskBO;
import com.zuji.remind.biz.repository.MsgPushTaskRepository;
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

    private MsgPushTaskRepository msgPushTaskRepository;

    @Autowired
    public void setMsgPushTaskService(MsgPushTaskRepository msgPushTaskRepository) {
        this.msgPushTaskRepository = msgPushTaskRepository;
    }

    /**
     * 事件标题，子类返回各自的事件名称（如"生日提醒"、"纪念日提醒"、"倒计时提醒"）。
     */
    protected abstract String getEventTitle();

    /**
     * 计算日期信息（阳历/农历），由子类实现具体的日期计算逻辑。
     */
    abstract void calculateDate(EventContextBO contextBO);

    /**
     * 获取邮件正文内容。
     */
    abstract String getEmailMsgContent(EventContextBO contextBO);

    /**
     * 获取钉钉消息内容。
     */
    abstract String getDingDingMsgContent(EventContextBO contextBO);

    /**
     * 处理纪念日任务数据，计算日期、判断是否需要通知并生成推送消息。
     *
     * @param bo 纪念日任务业务对象
     * @return 按推送方式分组的消息通知列表
     */
    public List<AggreNotifyBO> dealWithData(MemorialDayTaskBO bo) {
        EventContextBO contextBO = EventContextBO.init(bo);
        contextBO.setDateFactory(AbstractDateFactory.getInstance(bo.getDateType()));
        contextBO.setNotifyFactory(AbstractNotifyFactory.getInstance(bo.getEventType()));
        calculateDate(contextBO);
        calculateNotify(contextBO);
        return getSendMsg(contextBO);
    }

    /**
     * 将通知消息按推送方式持久化到消息队列，等待定时任务发送。
     *
     * @param notifyMap 按推送方式分组的消息通知映射
     */
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
            task.setMsgRequest(serializeMessage(remindWay, notifyList));
            taskList.add(task);
        });
        msgPushTaskRepository.addBatch(taskList);
    }

    private void calculateNotify(EventContextBO contextBO) {
        AbstractNotifyFactory notifyFactory = contextBO.getNotifyFactory();
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();
        AbstractNotifyFactory.NotifyBO notifyResult = notifyFactory.analyzeIsNotify(calculateResultBO.getThisYearDate(), originalDB.getRemindTimes());
        calculateResultBO.setIsNotify(notifyResult.isNotify());
        calculateResultBO.setIntervalDays(notifyResult.days());
    }

    private List<AggreNotifyBO> getSendMsg(EventContextBO contextBO) {
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        if (BooleanUtils.isNotTrue(originalDB.getStatusRemind())) {
            return Collections.emptyList();
        }
        if (BooleanUtils.isNotTrue(contextBO.getCalculateResultBO().getIsNotify())) {
            return Collections.emptyList();
        }

        List<AggreNotifyBO> list = Lists.newArrayListWithCapacity(3);
        for (RemindWayEnum remindWay : originalDB.getRemindWays()) {
            AggreNotifyBO notifyBO = new AggreNotifyBO();
            notifyBO.setEventTypeEnum(originalDB.getEventType());
            notifyBO.setRemindWayEnum(remindWay);
            notifyBO.setMsg(getMsgContent(remindWay, contextBO));
            list.add(notifyBO);
        }
        return list;
    }

    private String getMsgContent(RemindWayEnum remindWay, EventContextBO contextBO) {
        return switch (remindWay) {
            case EMAIL -> getEmailMsgContent(contextBO);
            case DING_DING -> getDingDingMsgContent(contextBO);
            default -> throw new UnsupportedOperationException("暂不支持[" + remindWay + "]方式");
        };
    }

    private String serializeMessage(RemindWayEnum remindWay, List<AggreNotifyBO> notifyList) {
        String content = notifyList.stream().map(AggreNotifyBO::getMsg).collect(Collectors.joining(
                remindWay == RemindWayEnum.EMAIL ? "  <br/>  " : "  \n  "));
        return switch (remindWay) {
            case EMAIL -> JSONUtil.toJsonStr(buildEmailBO(content));
            case DING_DING -> JSONUtil.toJsonStr(buildDingDingBody(content));
            default -> throw new UnsupportedOperationException("暂不支持[" + remindWay + "]方式");
        };
    }

    private MailBO buildEmailBO(String body) {
        String title = getEventTitle();
        MailBO bo = new MailBO();
        bo.setSubject(title);
        bo.setText(String.format("<h3>%s</h3> %s", title, body));
        return bo;
    }

    private OapiRobotSendRequest buildDingDingBody(String body) {
        String title = getEventTitle();
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(title);
        markdown.setText(String.format("## %s  \n  %s", title, body));
        OapiRobotSendRequest sendRequest = new OapiRobotSendRequest();
        sendRequest.setMsgtype("markdown");
        sendRequest.setMarkdown(markdown);
        return sendRequest;
    }
}
