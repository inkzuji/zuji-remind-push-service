package com.zuji.remind.biz.model.bo;

import com.zuji.remind.biz.enums.EventTypeEnum;
import com.zuji.remind.biz.enums.RemindWayEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 聚合消息.
 *
 * @author inkzuji@gmail.com
 * @since 2024-01-15 18:12
 **/
@Data
public class AggreNotifyBO implements Serializable {
    private static final long serialVersionUID = -4650400803587260618L;

    /**
     * 事件类型:1=生日; 2=纪念日; 3=倒计时;
     */
    private EventTypeEnum eventTypeEnum;

    /**
     * 提醒方式: 1=邮箱;2=钉钉;3=微信。
     */
    private RemindWayEnum remindWayEnum;

    private String msg;
}
