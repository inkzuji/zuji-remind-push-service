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
 * 生日.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-12 21:41
 **/
public class BirthdayEventFactory extends AbstractEventFactory {
    private final int dateType;
    private final String memorialDate;
    private final int isLeapMonth;
    private final String remindTimes;
    private volatile long intervalDays;
    private volatile LocalDate nextBirthdayDate;
    private volatile ChineseDate nextBirthdayChineseDate;

    public BirthdayEventFactory(int dateType, String memorialDate, int isLeapMonth, String remindTimes) {
        this.dateType = dateType;
        this.memorialDate = memorialDate;
        this.isLeapMonth = isLeapMonth;
        this.remindTimes = remindTimes;
    }

    @Override
    public boolean analyzeNotify() {
        AbstractDateFactory dateFactory = AbstractDateFactory.getInstance(dateType);
        ImmutablePair<LocalDate, ChineseDate> nextDatePair = dateFactory.analyzeNextNotifyDate(memorialDate, 1 == isLeapMonth);
        this.nextBirthdayDate = nextDatePair.getLeft();
        this.nextBirthdayChineseDate = nextDatePair.getRight();

        AbstractNotifyFactory notifyFactory = AbstractNotifyFactory.getInstance(EventTypeEnum.BIRTHDAY);
        ImmutablePair<Boolean, Long> notifyResult = notifyFactory.analyzeIsNotify(nextBirthdayDate, remindTimes);
        this.intervalDays = notifyResult.getRight();
        return notifyResult.getLeft();
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
        if (this.intervalDays > ZERO_LONG) {
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
                ", nextBirthday=" + nextBirthdayDate +
                "} " + super.toString();
    }

    /**
     * 拼接通知文本.
     */
    private String getDateFormat() {
        return String.format("%s %s (%s%s)", nextBirthdayDate, DateUtils.week2Str(nextBirthdayDate.getDayOfWeek()),
                this.nextBirthdayChineseDate.getChineseMonthName(), this.nextBirthdayChineseDate.getChineseDay());
    }

}
