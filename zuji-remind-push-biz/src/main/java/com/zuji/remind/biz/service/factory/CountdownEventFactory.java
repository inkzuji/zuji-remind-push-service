package com.zuji.remind.biz.service.factory;

import cn.hutool.core.date.ChineseDate;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zuji.remind.biz.enums.EventTypeEnum;
import com.zuji.remind.biz.untils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.zuji.remind.common.constant.CommonConstant.ZERO_LONG;

/**
 * 倒计时.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-12 21:41
 **/
public class CountdownEventFactory extends AbstractEventFactory {
    private final int dateType;
    private final String memorialDate;
    private final int isLeapMonth;
    private final String remindTimes;
    private volatile long intervalDays;
    private volatile LocalDate countdownDate;
    private volatile ChineseDate countdownChineseDate;

    public CountdownEventFactory(int dateType, String memorialDate, int isLeapMonth, String remindTimes) {
        this.dateType = dateType;
        this.memorialDate = memorialDate;
        this.isLeapMonth = isLeapMonth;
        this.remindTimes = remindTimes;
    }

    @Override
    public boolean analyzeNotify() {
        AbstractDateFactory dateFactory = AbstractDateFactory.getInstance(dateType);
        ImmutablePair<LocalDate, ChineseDate> currentNotifyDatePair = dateFactory.analyzeCurrentNotifyDate(memorialDate, 1 == isLeapMonth);
        this.countdownDate = currentNotifyDatePair.getLeft();
        this.countdownChineseDate = currentNotifyDatePair.getRight();

        AbstractNotifyFactory notifyFactory = AbstractNotifyFactory.getInstance(EventTypeEnum.COUNTDOWN);
        ImmutablePair<Boolean, Long> analyzeIsNotify = notifyFactory.analyzeIsNotify(countdownDate, remindTimes);
        this.intervalDays = analyzeIsNotify.getRight();
        return analyzeIsNotify.getLeft();
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
        list.add("### 倒计时提醒");
        list.add(String.format("**%s**", name));
        list.add(String.format("**倒计时**: %s", this.getDateFormat()));
        if (this.intervalDays > ZERO_LONG) {
            list.add(String.format("距离倒计时还有**%d**天！", this.intervalDays));
        } else {
            list.add("今天就是设定的倒计时哦！");
        }
        if (StringUtils.isNotBlank(taskDesc)) {
            list.add(String.format("> %s", taskDesc));
        }
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("倒计时提醒");
        markdown.setText(StringUtils.join(list, "  \n  "));
        OapiRobotSendRequest sendRequest = new OapiRobotSendRequest();
        sendRequest.setMsgtype("markdown");
        sendRequest.setMarkdown(markdown);
        return sendRequest;
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
     * 拼接通知文本.
     */
    private String getDateFormat() {
        return String.format("%s %s (%s%s)", countdownDate, DateUtils.week2Str(countdownDate.getDayOfWeek()),
                countdownChineseDate.getChineseMonthName(), countdownChineseDate.getChineseDay());
    }
}
