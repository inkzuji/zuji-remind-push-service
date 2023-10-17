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
 * 纪念日.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-12 21:41
 **/
public class AnniversaryFactory extends EventAbstractFactory {

    private final int dateType;
    private final String memorialDate;
    private final int isLeapMonth;
    private final String remindTimes;
    private volatile long intervalDays;
    private volatile LocalDate anniversaryDate;
    private volatile LocalDate nextAnniversaryDate;

    public AnniversaryFactory(int dateType, String memorialDate, int isLeapMonth, String remindTimes) {
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
        list.add("### 纪念日提醒");
        list.add(String.format("**%s**", name));
        list.add(String.format("**纪念日**: %s", this.getDateFormat()));
        if (this.intervalDays > 0) {
            list.add(String.format("已经**%d**天了！", anniversaryDate.until(LocalDate.now(), ChronoUnit.DAYS)));
        } else {
            list.add(String.format("%d周年快乐！", anniversaryDate.until(LocalDate.now(), ChronoUnit.YEARS)));
        }
        if (StringUtils.isNotBlank(taskDesc)) {
            list.add(String.format("> %s", taskDesc));
        }

        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("纪念日提醒");
        markdown.setText(StringUtils.join(list, "  \n  "));
        OapiRobotSendRequest sendRequest = new OapiRobotSendRequest();
        sendRequest.setMsgtype("markdown");
        sendRequest.setMarkdown(markdown);
        return sendRequest;
    }

    @Override
    public String toString() {
        return "AnniversaryFactory{" +
                "dateType=" + dateType +
                ", memorialDate='" + memorialDate + '\'' +
                ", isLeapMonth=" + isLeapMonth +
                ", remindTimes='" + remindTimes + '\'' +
                ", intervalDays=" + intervalDays +
                ", anniversaryDate=" + anniversaryDate +
                ", nextAnniversaryDate=" + nextAnniversaryDate +
                "} " + super.toString();
    }

    /**
     * 通用解析阳历方法。
     *
     * @return true=通知
     */
    private boolean analyzeSolarCalendar() {
        LocalDate now = LocalDate.now();
        anniversaryDate = LocalDateTimeUtil.parseDate(memorialDate, "yyyy-MM-dd");
        nextAnniversaryDate = anniversaryDate.withYear(now.getYear());

        // 当前天直接发送通知
        if (nextAnniversaryDate.isEqual(now)) {
            intervalDays = 0;
            return true;
        }
        if (nextAnniversaryDate.isBefore(now)) {
            nextAnniversaryDate = nextAnniversaryDate.plusYears(1);
        }
        intervalDays = now.until(nextAnniversaryDate, ChronoUnit.DAYS);

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
        ChineseDate chineseAnniversaryDate = new ChineseDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]), 1 == isLeapMonth);
        anniversaryDate = DateUtil.toLocalDateTime(chineseAnniversaryDate.getGregorianDate()).toLocalDate();
        int chineseYear = new ChineseDate(now).getChineseYear();

        ChineseDate nextChineseAnniversaryDate = new ChineseDate(chineseYear, chineseAnniversaryDate.getMonth(), chineseAnniversaryDate.getDay());
        nextAnniversaryDate = DateUtil.toLocalDateTime(nextChineseAnniversaryDate.getGregorianDate()).toLocalDate();
        if (nextAnniversaryDate.isEqual(now)) {
            intervalDays = 0;
            return true;
        }
        if (nextAnniversaryDate.isBefore(now)) {
            nextChineseAnniversaryDate = new ChineseDate(chineseYear + 1, chineseAnniversaryDate.getMonth(), chineseAnniversaryDate.getDay());
            nextAnniversaryDate = DateUtil.toLocalDateTime(nextChineseAnniversaryDate.getGregorianDate()).toLocalDate();
        }
        intervalDays = now.until(nextAnniversaryDate, ChronoUnit.DAYS);
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
        ChineseDate chineseDate = new ChineseDate(anniversaryDate);
        return String.format("%s %s (%s%s)", anniversaryDate, DateUtils.week2Str(anniversaryDate.getDayOfWeek()),
                chineseDate.getChineseMonthName(), chineseDate.getChineseDay());
    }
}
