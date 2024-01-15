package com.zuji.remind.biz.model.bo;

import cn.hutool.core.date.ChineseDate;
import com.zuji.remind.biz.component.datecal.AbstractDateFactory;
import com.zuji.remind.biz.component.notify.AbstractNotifyFactory;
import com.zuji.remind.biz.enums.DateTypeEnum;
import com.zuji.remind.biz.enums.EnableStatusEnum;
import com.zuji.remind.biz.enums.EventTypeEnum;
import com.zuji.remind.biz.enums.RemindWayEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 事件通知上下文消息.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 14:28
 **/
@Data
public class EventContextBO implements Serializable {
    private static final long serialVersionUID = 6534303007415909874L;

    private AbstractDateFactory dateFactory;

    private AbstractNotifyFactory notifyFactory;

    /**
     * 原始数据。
     */
    private OriginalDB originalDB;

    /**
     * 计算结果。
     */
    private CalculateResultBO calculateResultBO;

    /**
     * 原始消息。
     */
    @Data
    public static class OriginalDB implements Serializable {

        private static final long serialVersionUID = -8070307350464381267L;

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
         * 是否闰月：false=否；true=是
         */
        private Boolean isLeapMonth;

        /**
         * 日期
         */
        private String memorialDate;

        /**
         * 是否提醒: false=提醒; true=不提醒;
         */
        private Boolean statusRemind;

        /**
         * 提醒频率
         */
        private String remindTimes;

        /**
         * 提醒方式: 1=邮箱;2=钉钉;3=微信。
         */
        private List<RemindWayEnum> remindWays;

        public static OriginalDB from(MemorialDayTaskBO bo) {
            OriginalDB originalDB = new OriginalDB();
            originalDB.setEventType(bo.getEventType());
            originalDB.setName(bo.getName());
            originalDB.setTaskDesc(bo.getTaskDesc());
            originalDB.setDateType(bo.getDateType());
            originalDB.setIsLeapMonth(EnableStatusEnum.ENABLE == bo.getIsLeapMonth());
            originalDB.setMemorialDate(bo.getMemorialDate());
            originalDB.setStatusRemind(EnableStatusEnum.ENABLE == bo.getStatusRemind());
            originalDB.setRemindTimes(bo.getRemindTimes());
            originalDB.setRemindWays(bo.getRemindWays());
            return originalDB;
        }
    }

    /**
     * 计算结果.
     */
    @Data
    public static class CalculateResultBO {

        public static CalculateResultBO instance() {
            return new CalculateResultBO();
        }

        /**
         * 记录通知日期-阳历。
         */
        private LocalDate recordDate;

        /**
         * 记录通知日期-农历。
         */
        private ChineseDate recordChineseDate;

        /**
         * 今年通知日期-阳历。
         */
        private LocalDate thisYearDate;

        /**
         * 今年通知日期-农历。
         */
        private ChineseDate thisYearChineseDate;

        /**
         * 是否通知, true=通知
         */
        private Boolean isNotify;

        /**
         * 距离通知剩余天数。
         */
        private Long intervalDays;
    }

    public static EventContextBO init(MemorialDayTaskBO bo) {
        EventContextBO contextBO = new EventContextBO();
        contextBO.setOriginalDB(OriginalDB.from(bo));
        contextBO.setCalculateResultBO(CalculateResultBO.instance());
        return contextBO;
    }
}
