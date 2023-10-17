package com.zuji.remind.biz.service.factory;

import cn.hutool.core.date.ChineseDate;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zuji.remind.biz.untils.DateUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * 倒计时.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-12 21:41
 **/
public class CountdownFactory extends EventAbstractFactory {
    private final int dateType;
    private final String memorialDate;
    private final int isLeapMonth;
    private final String remindTimes;
    private volatile long intervalDays;
    private volatile LocalDate countdownDate;

    public CountdownFactory(int dateType, String memorialDate, int isLeapMonth, String remindTimes) {
        this.dateType = dateType;
        this.memorialDate = memorialDate;
        this.isLeapMonth = isLeapMonth;
        this.remindTimes = remindTimes;
    }

    @Override
    public boolean analyzeNotify() {
        // 阳历
        if (dateType == 1) {
            return this.analyzeSolarCalendar();
        } else {
            return this.analyzeChineseCalendar();
        }
    }

    /**
     * 获取推送钉钉机器人消息体。
     *
     * @param name     任务名称
     * @param taskDesc 任务描述
     * @return {@link OapiRobotSendRequest}
     */
    @Override
    public OapiRobotSendRequest getDingDingMessageBody(String name, String taskDesc) {
        return null;
    }

    @Override
    public String toString() {
        return "CountdownFactory{" +
                "dateType=" + dateType +
                ", memorialDate='" + memorialDate + '\'' +
                ", isLeapMonth=" + isLeapMonth +
                ", remindTimes='" + remindTimes + '\'' +
                ", intervalDays=" + intervalDays +
                ", countdownDate=" + countdownDate +
                "} " + super.toString();
    }

    /**
     * 通用解析阳历方法。
     *
     * @return true=通知
     */
    private boolean analyzeSolarCalendar() {
        LocalDate now = LocalDate.now();
        countdownDate = LocalDateTimeUtil.parseDate(memorialDate, "yyyy-MM-dd");

        // 当前天直接发送通知
        if (countdownDate.isEqual(now)) {
            intervalDays = 0;
            return true;
        }
        if (countdownDate.isBefore(now)) {
            intervalDays = countdownDate.until(now, ChronoUnit.DAYS);

            // 判断提前推送通知
            for (String day : remindTimes.split(StrUtil.COMMA)) {
                if (Objects.equals(intervalDays, Long.parseLong(day))) {
                    return true;
                }
            }
            return true;
        }
        intervalDays = 0L;
        return false;
    }

    /**
     * 通用解析农历方法。
     *
     * @return true=通知
     */
    private boolean analyzeChineseCalendar() {
        LocalDate now = LocalDate.now();
        String[] dates = memorialDate.split(StrUtil.DASHED);
        ChineseDate chineseCountdownDate = new ChineseDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]), 1 == isLeapMonth);
        countdownDate = DateUtil.toLocalDateTime(chineseCountdownDate.getGregorianDate()).toLocalDate();
        if (countdownDate.isEqual(now)) {
            intervalDays = 0;
            return true;
        }
        if (countdownDate.isBefore(now)) {
            intervalDays = countdownDate.until(now, ChronoUnit.DAYS);
            // 判断提前推送通知
            for (String day : remindTimes.split(StrUtil.COMMA)) {
                if (Objects.equals(intervalDays, Long.parseLong(day))) {
                    return true;
                }
            }
            return true;
        }
        intervalDays = 0L;
        return false;
    }

    /**
     * 拼接通知文本.
     */
    private String getDateFormat() {
        ChineseDate chineseDate = new ChineseDate(countdownDate);
        return String.format("%s %s (%s%s)", chineseDate, DateUtils.week2Str(countdownDate.getDayOfWeek()),
                chineseDate.getChineseMonthName(), chineseDate.getChineseDay());
    }
}
