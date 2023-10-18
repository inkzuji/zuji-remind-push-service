package com.zuji.remind.biz.model.dto;

import com.zuji.remind.biz.entity.MemorialDayTask;
import com.zuji.remind.common.exception.Asserts;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * 入参：提醒日任务DTO.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-10-18 14:27
 **/
public class MemorialDayTaskDTO {

    /**
     * 查询详情.
     */
    @Data
    public static class SaveTaskDTO implements Serializable {
        private static final long serialVersionUID = 7083428864879474409L;

        /**
         * 事件类型:1=生日; 2=纪念日; 3=倒计时;
         */
        @NotNull(message = "请选择事件类型")
        @Min(value = 1, message = "请选择事件类型")
        @Max(value = 3, message = "请选择事件类型")
        private Integer eventType;

        /**
         * 名称
         */
        @NotBlank(message = "请输入事件名称")
        private String name;

        /**
         * 描述
         */
        private String taskDesc;

        /**
         * 日期类型: 1=阳历; 2=农历
         */
        @NotNull(message = "请选择日期类型")
        @Min(value = 1, message = "请选择日期类型")
        @Max(value = 2, message = "请选择日期类型")
        private Integer dateType;

        /**
         * 是否闰月：0=否；1=是
         */
        private Integer isLeapMonth;

        /**
         * 日期
         */
        @NotBlank(message = "请选择日期")
        private String memorialDate;

        /**
         * 是否提醒: 0=提醒; 1=不提醒;
         */
        @NotNull(message = "请选择是否提醒")
        @Min(value = 0, message = "请选择是否提醒")
        @Max(value = 1, message = "请选择是否提醒")
        private Integer statusRemind;

        /**
         * 提醒频率
         */
        private String remindTimes;

        /**
         * 提醒方式: 1=邮箱;2=钉钉;3=微信;
         */
        private String remindWay;

        public static MemorialDayTask to(SaveTaskDTO dto) {
            if (Objects.isNull(dto)) {
                Asserts.fail("参数`dto`不能为空");
            }
            MemorialDayTask task = new MemorialDayTask();
            task.setEventType(dto.getEventType());
            task.setName(dto.getName());
            task.setTaskDesc(dto.getTaskDesc());
            task.setDateType(dto.getDateType());
            task.setIsLeapMonth(dto.getIsLeapMonth());
            task.setMemorialDate(dto.getMemorialDate());
            task.setStatusRemind(dto.getStatusRemind());
            task.setRemindTimes(dto.getRemindTimes());
            task.setRemindWay(dto.getRemindWay());
            return task;
        }
    }

}
