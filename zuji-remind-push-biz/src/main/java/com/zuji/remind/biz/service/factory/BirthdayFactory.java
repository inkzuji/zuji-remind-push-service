package com.zuji.remind.biz.service.factory;

import cn.hutool.core.date.ChineseDate;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zuji.remind.biz.untils.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 生日.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-12 21:41
 **/
public class BirthdayFactory extends EventAbstractFactory {
    private final int dateType;
    private final String memorialDate;
    private final int isLeapMonth;
    private final String remindTimes;
    private volatile long intervalDays;
    private volatile LocalDate nextBirthday;

    public BirthdayFactory(int dateType, String memorialDate, int isLeapMonth, String remindTimes) {
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
        List<Object> list = new ArrayList<>();
        list.add("### 生日提醒");
        list.add(String.format("**%s**", name));
        list.add(String.format("**生日**: %s", this.getDateFormat()));
        if (this.intervalDays > 0) {
            list.add(String.format("距离生日还有**%d**天！", this.intervalDays));
        } else {
            list.add("生日快乐！");
        }
        if (StringUtils.isNotBlank(taskDesc)) {
            list.add(String.format("> %s", taskDesc));
        }

        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("生日提醒");
        markdown.setText(StringUtils.join(list, "  \n  "));
        OapiRobotSendRequest sendRequest = new OapiRobotSendRequest();
        sendRequest.setMsgtype("markdown");
        sendRequest.setMarkdown(markdown);
        return sendRequest;
    }

    @Override
    public String toString() {
        return "BirthdayFactory{" +
                "dateType=" + dateType +
                ", memorialDate='" + memorialDate + '\'' +
                ", isLeapMonth=" + isLeapMonth +
                ", remindTimes='" + remindTimes + '\'' +
                ", intervalDays=" + intervalDays +
                ", nextBirthday=" + nextBirthday +
                "} " + super.toString();
    }

    /**
     * 通用解析阳历方法。
     *
     * @return true=通知
     */
    private boolean analyzeSolarCalendar() {
        LocalDate now = LocalDate.now();
        LocalDate birthday = LocalDateTimeUtil.parseDate(memorialDate, "yyyy-MM-dd");
        nextBirthday = birthday.withYear(now.getYear());

        // 当前天直接发送通知
        if (nextBirthday.isEqual(now)) {
            intervalDays = 0;
            return true;
        }
        if (nextBirthday.isBefore(now)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        intervalDays = now.until(nextBirthday, ChronoUnit.DAYS);

        // 判断提前推送通知
        for (String day : remindTimes.split(StrUtil.COMMA)) {
            if (Objects.equals(intervalDays, Long.parseLong(day))) {
                return true;
            }
        }
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
        ChineseDate chineseBirthday = new ChineseDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]), 1 == isLeapMonth);
        int chineseYear = new ChineseDate(now).getChineseYear();

        ChineseDate nextChineseBirthday = new ChineseDate(chineseYear, chineseBirthday.getMonth(), chineseBirthday.getDay());
        nextBirthday = DateUtil.toLocalDateTime(nextChineseBirthday.getGregorianDate()).toLocalDate();
        if (nextBirthday.isEqual(now)) {
            intervalDays = 0;
            return true;
        }
        if (nextBirthday.isBefore(now)) {
            nextChineseBirthday = new ChineseDate(chineseYear + 1, chineseBirthday.getMonth(), chineseBirthday.getDay());
            nextBirthday = DateUtil.toLocalDateTime(nextChineseBirthday.getGregorianDate()).toLocalDate();
        }
        intervalDays = now.until(nextBirthday, ChronoUnit.DAYS);
        for (String day : remindTimes.split(StrUtil.COMMA)) {
            if (Objects.equals(intervalDays, Long.parseLong(day))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 拼接通知文本.
     */
    private String getDateFormat() {
        ChineseDate chineseDate = new ChineseDate(nextBirthday);
        return String.format("%s %s (%s%s)", nextBirthday, DateUtils.week2Str(nextBirthday.getDayOfWeek()),
                chineseDate.getChineseMonthName(), chineseDate.getChineseDay());
    }

}
