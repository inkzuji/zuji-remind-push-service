package com.zuji.remind.biz.service.factory;

import cn.hutool.core.date.ChineseDate;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zuji.remind.biz.component.datecal.AbstractDateFactory;
import com.zuji.remind.biz.component.notify.AbstractNotifyFactory;
import com.zuji.remind.biz.model.bo.EventContextBO;
import com.zuji.remind.biz.model.bo.MailBO;
import com.zuji.remind.biz.untils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.zuji.remind.common.constant.CommonConstant.ZERO_LONG;

/**
 * 倒计时.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-12 21:41
 **/
@Service
public class CountdownEventFactory extends AbstractEventFactory {
    @Override
    void calculateDate(EventContextBO contextBO) {
        AbstractDateFactory dateFactory = contextBO.getDateFactory();
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        ImmutablePair<LocalDate, ChineseDate> recordDatePair = dateFactory.analyzeCurrentNotifyDate(originalDB.getMemorialDate(), originalDB.getIsLeapMonth());
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();
        calculateResultBO.setRecordDate(recordDatePair.getLeft());
        calculateResultBO.setRecordChineseDate(recordDatePair.getRight());
        calculateResultBO.setThisYearDate(recordDatePair.getLeft());
        calculateResultBO.setThisYearChineseDate(recordDatePair.getRight());
    }

    @Override
    void calculateNotify(EventContextBO contextBO) {
        AbstractNotifyFactory notifyFactory = contextBO.getNotifyFactory();
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();
        ImmutablePair<Boolean, Long> notifyResult = notifyFactory.analyzeIsNotify(calculateResultBO.getThisYearDate(), originalDB.getRemindTimes());
        calculateResultBO.setIsNotify(notifyResult.getLeft());
        calculateResultBO.setIntervalDays(notifyResult.getRight());
    }

    @Override
    MailBO getEmailBO(EventContextBO contextBO) {
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();

        StringBuffer bf = new StringBuffer();
        bf.append("<html><h3>倒计时提醒</h3>");
        bf.append("<p>").append(originalDB.getName()).append("</p>");
        if (calculateResultBO.getIntervalDays() > ZERO_LONG) {
            bf.append(String.format("距离倒计时还有**%d**天！", calculateResultBO.getIntervalDays()));
        } else {
            bf.append("<p>今天就是设定的倒计时哦！</p>");
        }
        if (StringUtils.isNotBlank(originalDB.getTaskDesc())) {
            bf.append("<p>").append(originalDB.getTaskDesc()).append("</p>");
        }
        bf.append("</html>");
        MailBO bo = new MailBO();
        bo.setSubject("倒计时提醒");
        bo.setText(bf.toString());
        return bo;
    }

    @Override
    OapiRobotSendRequest getDingDingMessageBody(EventContextBO contextBO) {
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();

        List<Object> list = new ArrayList<>();
        list.add("### 倒计时提醒");
        list.add(String.format("**%s**", originalDB.getName()));
        list.add(String.format("**倒计时**: %s", String.format("%s %s (%s%s)", calculateResultBO.getThisYearDate(), DateUtils.week2Str(calculateResultBO.getThisYearDate().getDayOfWeek()),
                calculateResultBO.getThisYearChineseDate().getChineseMonthName(), calculateResultBO.getThisYearChineseDate().getChineseDay())));
        if (calculateResultBO.getIntervalDays() > ZERO_LONG) {
            list.add(String.format("距离倒计时还有**%d**天！", calculateResultBO.getIntervalDays()));
        } else {
            list.add("今天就是设定的倒计时哦！");
        }
        if (StringUtils.isNotBlank(originalDB.getTaskDesc())) {
            list.add(String.format("> %s", originalDB.getTaskDesc()));
        }
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("倒计时提醒");
        markdown.setText(StringUtils.join(list, "  \n  "));
        OapiRobotSendRequest sendRequest = new OapiRobotSendRequest();
        sendRequest.setMsgtype("markdown");
        sendRequest.setMarkdown(markdown);
        return sendRequest;
    }
}
