package com.zuji.remind.biz.service.factory;

import cn.hutool.core.date.ChineseDate;
import cn.hutool.core.util.StrUtil;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zuji.remind.biz.entity.MsgPushWay;
import com.zuji.remind.biz.enums.EventTypeEnum;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.model.bo.MailBO;
import com.zuji.remind.biz.untils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.zuji.remind.common.constant.CommonConstant.ZERO_LONG;

/**
 * 生日.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-12 21:41
 **/
@Service
public class BirthdayEventFactory extends AbstractEventFactory {
    private volatile long intervalDays;
    private volatile LocalDate nextBirthdayDate;
    private volatile ChineseDate nextBirthdayChineseDate;

    @Override
    public boolean analyzeNotify(int dateType, String memorialDate, int isLeapMonth, String remindTimes) {
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
     * 发送消息
     *
     * @param remindWay 发送方式
     * @param name      名称
     * @param taskDesc  描述
     */
    @Override
    public void sendMsg(Map<Integer, MsgPushWay> pushWayMap, String remindWay, String name, String taskDesc) {
        for (String way : remindWay.split(StrUtil.COMMA)) {
            RemindWayEnum remindWayEnum = RemindWayEnum.getByCode(Integer.parseInt(way));
            switch (remindWayEnum) {
                case EMAIL:
                    super.sendEmail(this.getEmailBO(name, taskDesc), pushWayMap.get(RemindWayEnum.EMAIL.getCode()).getPushContext());
                    break;
                case DING_DING:
                    super.sendDingDing(this.getDingDingMessageBody(name, taskDesc), pushWayMap.get(RemindWayEnum.DING_DING.getCode()).getPushContext());
                    break;
                case WECHAT:
                    break;
                default:
                    throw new RuntimeException("暂不支持[" + way + "]方式");
            }
        }
    }

    private MailBO getEmailBO(String name, String taskDesc) {
        StringBuffer bf = new StringBuffer();
        bf.append("<html><h3>生日提醒</h3>");
        bf.append("<p>生日:").append(name).append("</p>");
        if (this.intervalDays > ZERO_LONG) {
            bf.append(String.format("距离生日还有**%d**天！", this.intervalDays));
        } else {
            bf.append("<p>生日快乐！</p>");
        }
        if (StringUtils.isNotBlank(taskDesc)) {
            bf.append("<p>").append(taskDesc).append("</p>");
        }
        bf.append("</html>");
        MailBO bo = new MailBO();
        bo.setSubject("生日提醒");
        bo.setText(bf.toString());
        return bo;
    }

    /**
     * 获取推送钉钉机器人消息体。
     *
     * @param name     任务名称
     * @param taskDesc 任务描述
     * @return {@link OapiRobotSendRequest}
     */
    private OapiRobotSendRequest getDingDingMessageBody(String name, String taskDesc) {
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

    /**
     * 拼接通知文本.
     */
    private String getDateFormat() {
        return String.format("%s %s (%s%s)", nextBirthdayDate, DateUtils.week2Str(nextBirthdayDate.getDayOfWeek()),
                this.nextBirthdayChineseDate.getChineseMonthName(), this.nextBirthdayChineseDate.getChineseDay());
    }

}
