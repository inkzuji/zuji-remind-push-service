package com.zuji.remind.biz.model.bo;

import cn.hutool.core.util.StrUtil;
import com.zuji.remind.biz.entity.MemorialDayTask;
import com.zuji.remind.biz.enums.DateTypeEnum;
import com.zuji.remind.biz.enums.EventTypeEnum;
import com.zuji.remind.biz.enums.RemindWayEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    private EventTypeEnum eventType;

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
    private DateTypeEnum dateType;

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
    private List<RemindWayEnum> remindWays;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public static MemorialDayTaskBO from(MemorialDayTask task) {
        List<RemindWayEnum> remindWays;
        if (StrUtil.isBlank(task.getRemindWay())) {
            remindWays = Collections.emptyList();
        } else {
            remindWays = StrUtil.split(task.getRemindWay(), StrUtil.COMMA).stream()
                    .map(v -> RemindWayEnum.getByCode(Integer.parseInt(v)))
                    .collect(Collectors.toList());
        }
        MemorialDayTaskBO taskBO = new MemorialDayTaskBO();
        taskBO.setId(task.getId());
        taskBO.setEventType(EventTypeEnum.getByCode(task.getEventType()));
        taskBO.setName(task.getName());
        taskBO.setTaskDesc(task.getTaskDesc());
        taskBO.setDateType(DateTypeEnum.getByType(task.getDateType()));
        taskBO.setIsLeapMonth(task.getIsLeapMonth());
        taskBO.setMemorialDate(task.getMemorialDate());
        taskBO.setStatusRemind(task.getStatusRemind());
        taskBO.setRemindTimes(task.getRemindTimes());
        taskBO.setRemindWays(remindWays);
        taskBO.setUpdateTime(task.getUpdateTime());
        taskBO.setCreateTime(task.getCreateTime());
        return taskBO;

    }
}
