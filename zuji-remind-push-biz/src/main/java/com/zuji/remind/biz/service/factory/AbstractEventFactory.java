package com.zuji.remind.biz.service.factory;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zuji.remind.biz.enums.EventTypeEnum;

/**
 * 抽象类型.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-11 22:58
 **/

public abstract class AbstractEventFactory {

    public static AbstractEventFactory getInstance(int type, int dateType, String memorialDate, int isLeapMonth, String remindTimes) {
        EventTypeEnum typeEnum = EventTypeEnum.getByCode(type);
        switch (typeEnum) {
            case BIRTHDAY:
                return new BirthdayEventFactory(dateType, memorialDate, isLeapMonth, remindTimes);
            case ANNIVERSARY:
                return new AnniversaryEventFactory(dateType, memorialDate, isLeapMonth, remindTimes);
            case COUNTDOWN:
                return new CountdownEventFactory(dateType, memorialDate, isLeapMonth, remindTimes);
            default:
                throw new RuntimeException("暂不支持[" + type + "]类型");
        }
    }

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
