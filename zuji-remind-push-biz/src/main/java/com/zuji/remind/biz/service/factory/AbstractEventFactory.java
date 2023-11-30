package com.zuji.remind.biz.service.factory;

import cn.hutool.json.JSONUtil;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zuji.remind.biz.client.DingDingPushClient;
import com.zuji.remind.biz.client.EmailPushClient;
import com.zuji.remind.biz.entity.MsgPushWay;
import com.zuji.remind.biz.model.bo.MailBO;
import com.zuji.remind.biz.model.bo.MsgPushWayBO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 抽象类型.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-11 22:58
 **/

public abstract class AbstractEventFactory {

    protected EmailPushClient emailPushClient;
    protected DingDingPushClient dingDingPushClient;

    @Autowired
    public void setEmailPushClient(EmailPushClient emailPushClient) {
        this.emailPushClient = emailPushClient;
    }

    @Autowired
    public void setDingDingPushClient(DingDingPushClient dingDingPushClient) {
        this.dingDingPushClient = dingDingPushClient;
    }

    protected void sendEmail(MailBO bo, String userInfo) {
        MsgPushWayBO.EmailWayBO wayBO = JSONUtil.toBean(userInfo, MsgPushWayBO.EmailWayBO.class);
        bo.setTo(wayBO.getTo());
        bo.setCc(wayBO.getCc());
        emailPushClient.sendMessage(bo);
    }

    protected void sendDingDing(OapiRobotSendRequest requestBody, String sendInfo) {
        MsgPushWayBO.DingDingBO wayBO = JSONUtil.toBean(sendInfo, MsgPushWayBO.DingDingBO.class);
        dingDingPushClient.send(requestBody, wayBO);
    }

    public abstract boolean analyzeNotify(int dateType, String memorialDate, int isLeapMonth, String remindTimes);

    /**
     * 发送消息
     *
     * @param remindWay 发送方式
     * @param name      名称
     * @param taskDesc  描述
     */
    public abstract void sendMsg(Map<Integer, MsgPushWay> pushWayMap, String remindWay, String name, String taskDesc);

    @Override
    public String toString() {
        return "EventAbstractFactory{}";
    }
}
