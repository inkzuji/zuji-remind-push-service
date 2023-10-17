package com.zuji.remind.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 倒数记录任务.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-11 22:07
 **/
@Data
@TableName(value = "memorial_day_task")
public class MemorialDayTask implements Serializable {
    private static final long serialVersionUID = -1617432914419346997L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 是否闰月：0=否；1=是
     */
    private Integer isLeapMonth;

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
     * 提醒方式: 1=邮箱;2=钉钉;3=微信;
     */
    private String remindWay;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否删除: 0=否; 1=是;
     */
    @TableLogic(value = "0", delval = "1")
    private Integer isDelete;
}