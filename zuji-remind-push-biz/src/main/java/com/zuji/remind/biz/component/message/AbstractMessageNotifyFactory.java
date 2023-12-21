package com.zuji.remind.biz.component.message;

import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.model.bo.SendMessageBO;
import com.zuji.remind.common.api.CommonResult;

/**
 * 消息通知抽象类.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 11:43
 **/
public abstract class AbstractMessageNotifyFactory {

    /**
     * 通知方式。
     *
     * @return.
     */
    abstract RemindWayEnum getRemindWay();

    /**
     * 发送消息。
     */
    public abstract CommonResult<Void> send(SendMessageBO bo);
}
