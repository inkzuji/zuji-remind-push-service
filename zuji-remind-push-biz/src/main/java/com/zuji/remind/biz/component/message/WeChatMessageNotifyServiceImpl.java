package com.zuji.remind.biz.component.message;

import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.model.bo.SendMessageBO;
import com.zuji.remind.common.api.CommonResult;
import org.springframework.stereotype.Service;

/**
 * 微信消息通知.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 14:09
 **/
@Service
public class WeChatMessageNotifyServiceImpl extends AbstractMessageNotifyFactory {
    @Override
    RemindWayEnum getRemindWay() {
        return RemindWayEnum.WECHAT;
    }

    @Override
    public CommonResult<Void> send(SendMessageBO bo) {
        return CommonResult.success();
    }
}
