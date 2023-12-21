package com.zuji.remind.biz.component.message;

import com.zuji.remind.biz.client.EmailPushClient;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.model.bo.MailBO;
import com.zuji.remind.biz.model.bo.MsgPushWayBO;
import com.zuji.remind.biz.model.bo.SendMessageBO;
import com.zuji.remind.common.api.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 发送邮件消息.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 13:55
 **/
@Service
public class EmailMessageNotifyServiceImpl extends AbstractMessageNotifyFactory {

    private EmailPushClient emailPushClient;

    @Autowired
    public void setEmailPushClient(EmailPushClient emailPushClient) {
        this.emailPushClient = emailPushClient;
    }

    @Override
    RemindWayEnum getRemindWay() {
        return RemindWayEnum.EMAIL;
    }

    @Override
    public CommonResult<Void> send(SendMessageBO bo) {
        MailBO mailBO = bo.getMailBO();
        MsgPushWayBO.EmailWayBO wayBO = (MsgPushWayBO.EmailWayBO) bo.getWayBO();
        mailBO.setTo(wayBO.getTo());
        mailBO.setCc(wayBO.getCc());
        return emailPushClient.sendMessage(mailBO);
    }
}
