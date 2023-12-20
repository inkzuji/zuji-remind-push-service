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
 * 生日.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-12 21:41
 **/
@Service
public class BirthdayEventFactory extends AbstractEventFactory {
    
    @Override
    void calculateDate(EventContextBO contextBO) {
        AbstractDateFactory dateFactory = contextBO.getDateFactory();
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        ImmutablePair<LocalDate, ChineseDate> recordDatePair = dateFactory.analyzeCurrentNotifyDate(originalDB.getMemorialDate(), originalDB.getIsLeapMonth());
        ImmutablePair<LocalDate, ChineseDate> nextDatePair = dateFactory.analyzeNextNotifyDate(originalDB.getMemorialDate(), originalDB.getIsLeapMonth());
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();
        calculateResultBO.setRecordDate(recordDatePair.getLeft());
        calculateResultBO.setRecordChineseDate(recordDatePair.getRight());
        calculateResultBO.setThisYearDate(nextDatePair.getLeft());
        calculateResultBO.setThisYearChineseDate(nextDatePair.getRight());
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
        bf.append("<html><h3>生日提醒</h3>");
        bf.append("<p>生日:").append(originalDB.getName()).append("</p>");
        if (calculateResultBO.getIntervalDays() > ZERO_LONG) {
            bf.append(String.format("距离生日还有**%d**天！", calculateResultBO.getIntervalDays()));
        } else {
            bf.append("<p>生日快乐！</p>");
        }
        if (StringUtils.isNotBlank(originalDB.getTaskDesc())) {
            bf.append("<p>").append(originalDB.getTaskDesc()).append("</p>");
        }
        bf.append("</html>");
        MailBO bo = new MailBO();
        bo.setSubject("生日提醒");
        bo.setText(bf.toString());
        return bo;
    }

    @Override
    OapiRobotSendRequest getDingDingMessageBody(EventContextBO contextBO) {
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();
        LocalDate thisYearDate = calculateResultBO.getThisYearDate();
        ChineseDate thisYearChineseDate = calculateResultBO.getThisYearChineseDate();

        List<Object> list = new ArrayList<>();
        list.add("### 生日提醒");
        list.add(String.format("**%s**", originalDB.getName()));
        list.add(String.format("**生日**: %s", String.format("%s %s (%s%s)", thisYearDate, DateUtils.week2Str(thisYearDate.getDayOfWeek()),
                thisYearChineseDate.getChineseMonthName(), thisYearChineseDate.getChineseDay())));
        if (calculateResultBO.getIntervalDays() > ZERO_LONG) {
            list.add(String.format("距离生日还有**%d**天！", calculateResultBO.getIntervalDays()));
        } else {
            list.add("生日快乐！");
        }
        if (StringUtils.isNotBlank(originalDB.getTaskDesc())) {
            list.add(String.format("> %s", originalDB.getTaskDesc()));
        }

        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("生日提醒");
        markdown.setText(StringUtils.join(list, "  \n  "));
        OapiRobotSendRequest sendRequest = new OapiRobotSendRequest();
        sendRequest.setMsgtype("markdown");
        sendRequest.setMarkdown(markdown);
        return sendRequest;
    }
}
