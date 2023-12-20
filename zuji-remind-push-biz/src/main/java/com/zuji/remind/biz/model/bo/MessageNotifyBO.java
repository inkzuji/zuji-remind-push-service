package com.zuji.remind.biz.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息通知bo.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 11:41
 **/
@Data
public class MessageNotifyBO implements Serializable {
    private static final long serialVersionUID = 1094934006566059770L;

    /**
     * 提醒方式: 1=邮箱;2=钉钉;3=微信;
     */
    private String remindWay;

    /**
     * 名称。
     */
    private String name;

    /**
     * 描述
     */
    private String taskDesc;
}
