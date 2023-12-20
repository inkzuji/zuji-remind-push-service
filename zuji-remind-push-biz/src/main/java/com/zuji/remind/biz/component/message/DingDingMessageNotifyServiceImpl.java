package com.zuji.remind.biz.component.message;

import com.zuji.remind.biz.client.DingDingPushClient;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.model.bo.MsgPushWayBO;
import com.zuji.remind.biz.model.bo.SendMessageBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 钉钉消息推送通知service.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 14:04
 **/
@Service
public class DingDingMessageNotifyServiceImpl extends AbstractMessageNotifyFactory {

    private DingDingPushClient dingDingPushClient;

    @Autowired
    public void setDingDingPushClient(DingDingPushClient dingDingPushClient) {
        this.dingDingPushClient = dingDingPushClient;
    }

    @Override
    RemindWayEnum getRemindWay() {
        return RemindWayEnum.DING_DING;
    }

    @Override
    public void send(SendMessageBO bo) {
        MsgPushWayBO.DingDingBO wayBO = (MsgPushWayBO.DingDingBO) bo.getWayBO();
        dingDingPushClient.send(bo.getDingDingRequest(), wayBO);
    }
}
