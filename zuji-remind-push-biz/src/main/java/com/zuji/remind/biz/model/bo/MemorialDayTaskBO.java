package com.zuji.remind.biz.model.bo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 倒数纪念日bo.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-11 22:25
 **/
@Data
public class MemorialDayTaskBO implements Serializable {
    private static final long serialVersionUID = 5208506487578832523L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 事件类型:1=生日; 2=纪念日; 3=倒计时;
     */
    private Integer eventType;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String taskDesc;

    /**
     * 日期类型: 1=阳历; 2=农历
     */
    private Integer dateType;

    /**
     * 日期
     */
    private String memorialDate;

    /**
     * 是否提醒: 0=提醒; 1=不提醒;
     */
    private Integer statusRemind;

    /**
     * 提醒频率
     */
    private String remindTimes;

    /**
     * 提醒方式: 0=全部;1=邮箱;2=钉钉
     */
    private Integer remindWay;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
