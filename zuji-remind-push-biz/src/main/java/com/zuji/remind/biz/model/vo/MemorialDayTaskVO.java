package com.zuji.remind.biz.model.vo;

import com.zuji.remind.biz.entity.MemorialDayTask;
import com.zuji.remind.common.api.ResultCode;
import com.zuji.remind.common.exception.Asserts;
import lombok.Data;

import java.io.Serializable;

/**
 * 出参.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-23 16:18
 **/
public class MemorialDayTaskVO {

    @Data
    public static class TaskVO implements Serializable {
        private static final long serialVersionUID = -8765735527549283442L;
        
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

        public static TaskVO from(MemorialDayTask task) {
            if (null == task) {
                Asserts.fail(ResultCode.VALIDATE_FAILED);
            }
            TaskVO taskVO = new TaskVO();
            taskVO.setEventType(task.getEventType());
            taskVO.setName(task.getName());
            taskVO.setTaskDesc(task.getTaskDesc());
            taskVO.setDateType(task.getDateType());
            taskVO.setIsLeapMonth(task.getIsLeapMonth());
            taskVO.setMemorialDate(task.getMemorialDate());
            taskVO.setStatusRemind(task.getStatusRemind());
            taskVO.setRemindTimes(task.getRemindTimes());
            taskVO.setRemindWay(task.getRemindWay());
            return taskVO;
        }
    }

}
