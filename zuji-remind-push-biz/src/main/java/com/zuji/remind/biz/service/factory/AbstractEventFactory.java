package com.zuji.remind.biz.service.factory;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zuji.remind.biz.component.datecal.AbstractDateFactory;
import com.zuji.remind.biz.component.message.AbstractMessageNotifyFactory;
import com.zuji.remind.biz.component.message.MessageNotifyComponent;
import com.zuji.remind.biz.component.notify.AbstractNotifyFactory;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.model.bo.EventContextBO;
import com.zuji.remind.biz.model.bo.MailBO;
import com.zuji.remind.biz.model.bo.MemorialDayTaskBO;
import com.zuji.remind.biz.model.bo.MsgPushWayBO;
import com.zuji.remind.biz.model.bo.SendMessageBO;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 抽象类型.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-11 22:58
 **/
public abstract class AbstractEventFactory {

    private MessageNotifyComponent messageNotifyComponent;

    @Autowired
    public void setMessageNotifyComponent(MessageNotifyComponent messageNotifyComponent) {
        this.messageNotifyComponent = messageNotifyComponent;
    }

    public void dealWithData(MemorialDayTaskBO bo, List<MsgPushWayBO> msgPushWayBOList) {
        EventContextBO contextBO = EventContextBO.init(bo, msgPushWayBOList);
        contextBO.setDateFactory(AbstractDateFactory.getInstance(bo.getDateType()));
        contextBO.setNotifyFactory(AbstractNotifyFactory.getInstance(bo.getEventType()));
        calculateDate(contextBO);
        calculateNotify(contextBO);
        sendMessage(contextBO);
    }

    private void sendMessage(EventContextBO contextBO) {
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();
        if (BooleanUtils.isNotFalse(originalDB.getStatusRemind())) {
            return;
        }
        if (BooleanUtils.isNotTrue(calculateResultBO.getIsNotify())) {
            return;
        }
        SendMessageBO messageBO;
        AbstractMessageNotifyFactory notifyFactory;
        for (MsgPushWayBO msgPushWayBO : originalDB.getRemindWayBOList()) {
            switch (msgPushWayBO.getPushType()) {
                case EMAIL:
                    messageBO = new SendMessageBO();
                    messageBO.setMailBO(getEmailBO(contextBO));
                    messageBO.setWayBO(msgPushWayBO.getPushRequestParam());
                    notifyFactory = messageNotifyComponent.getByRemindWay(RemindWayEnum.EMAIL);
                    notifyFactory.send(messageBO);
                    break;
                case DING_DING:
                    messageBO = new SendMessageBO();
                    messageBO.setDingDingRequest(getDingDingMessageBody(contextBO));
                    messageBO.setWayBO(msgPushWayBO.getPushRequestParam());
                    notifyFactory = messageNotifyComponent.getByRemindWay(RemindWayEnum.DING_DING);
                    notifyFactory.send(messageBO);
                    break;
                case WECHAT:
                default:
                    throw new RuntimeException("暂不支持[" + msgPushWayBO.getPushType() + "]方式");
            }
        }
    }

    abstract void calculateDate(EventContextBO contextBO);

    abstract void calculateNotify(EventContextBO contextBO);

    abstract MailBO getEmailBO(EventContextBO contextBO);

    abstract OapiRobotSendRequest getDingDingMessageBody(EventContextBO contextBO);
}
