package com.zuji.remind.biz.model.dto;

import cn.hutool.core.util.StrUtil;
import com.zuji.remind.biz.dao.entity.MemorialDayTask;
import com.zuji.remind.biz.enums.EnableStatusEnum;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.common.exception.Asserts;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 入参：提醒日任务DTO.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 14:27
 **/
public class MemorialDayTaskDTO {

    /**
     * 查询详情.
     */
    @Data
    public static class SaveTaskDTO implements Serializable {
        @Serial
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
         * 是否提醒: 0=不提醒; 1=提醒;
         */
        @NotNull(message = "请选择是否提醒")
        @Min(value = 0, message = "请选择是否提醒")
        @Max(value = 1, message = "请选择是否提醒")
        private Integer statusRemind;

        /**
         * 提醒天数，多个整数以英文逗号分隔；启用提醒时必填。
         */
        private String remindTimes;

        /**
         * 提醒方式: 1=邮箱;2=钉钉，多个渠道以英文逗号分隔；启用提醒时必填。
         */
        private String remindWay;

        public static MemorialDayTask to(SaveTaskDTO dto) {
            if (Objects.isNull(dto)) {
                Asserts.fail("参数`dto`不能为空");
            }
            dto.validateReminder();
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

        private void validateReminder() {
            boolean enabled = Objects.equals(statusRemind, EnableStatusEnum.ENABLE.getCode());
            if (StrUtil.isBlank(remindTimes)) {
                if (enabled) {
                    Asserts.fail("启用提醒时请输入提醒天数");
                }
            } else {
                for (String day : remindTimes.split(StrUtil.COMMA, -1)) {
                    try {
                        Long.parseLong(day);
                    } catch (NumberFormatException e) {
                        Asserts.fail("提醒天数必须是英文逗号分隔的整数，且不能超出long范围");
                    }
                }
            }

            if (StrUtil.isBlank(remindWay)) {
                if (enabled) {
                    Asserts.fail("启用提醒时请选择提醒方式");
                }
            } else {
                for (String way : remindWay.split(StrUtil.COMMA, -1)) {
                    int code;
                    try {
                        code = Integer.parseInt(way);
                    } catch (NumberFormatException e) {
                        Asserts.fail("提醒方式必须是英文逗号分隔的渠道编码");
                        return;
                    }
                    if (code != RemindWayEnum.EMAIL.getCode() && code != RemindWayEnum.DING_DING.getCode()) {
                        Asserts.fail("当前仅支持邮箱和钉钉提醒");
                    }
                }
            }
        }
    }

}
