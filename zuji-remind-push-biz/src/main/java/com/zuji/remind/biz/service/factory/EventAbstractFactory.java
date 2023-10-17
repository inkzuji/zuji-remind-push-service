package com.zuji.remind.biz.service.factory;

import com.dingtalk.api.request.OapiRobotSendRequest;

/**
 * 抽象类型.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-11 22:58
 **/

public abstract class EventAbstractFactory {

    public abstract boolean analyzeNotify();


    /**
     * 获取推送钉钉机器人消息体。
     *
     * @param name     任务名称
     * @param taskDesc 任务描述
     * @return {@link OapiRobotSendRequest}
     */
    public abstract OapiRobotSendRequest getDingDingMessageBody(String name, String taskDesc);

    @Override
    public String toString() {
        return "EventAbstractFactory{}";
    }
}
