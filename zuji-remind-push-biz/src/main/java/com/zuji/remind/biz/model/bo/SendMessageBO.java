package com.zuji.remind.biz.model.bo;

import com.dingtalk.api.request.OapiRobotSendRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送消息BO.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 13:49
 **/
@Data
public class SendMessageBO implements Serializable {
    private static final long serialVersionUID = 5283627186026914689L;

    /**
     * 邮件消息。
     */
    private MailBO mailBO;

    /**
     * 钉钉消息。
     */
    private OapiRobotSendRequest dingDingRequest;

    /**
     * 通知内容。
     */
    private MsgPushWayBO.WayBO wayBO;
}
