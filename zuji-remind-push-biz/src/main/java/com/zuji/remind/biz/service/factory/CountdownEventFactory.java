package com.zuji.remind.biz.service.factory;

import com.zuji.remind.biz.component.datecal.AbstractDateFactory;
import com.zuji.remind.biz.model.bo.EventContextBO;
import com.zuji.remind.biz.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
    protected String getEventTitle() {
        return "倒计时提醒";
    }

    @Override
    void calculateDate(EventContextBO contextBO) {
        AbstractDateFactory dateFactory = contextBO.getDateFactory();
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        AbstractDateFactory.DateBO recordDateBO = dateFactory.calculateCurrentDate(originalDB.getMemorialDate(), originalDB.getIsLeapMonth());
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();
        calculateResultBO.setRecordDate(recordDateBO.solarDate());
        calculateResultBO.setRecordChineseDate(recordDateBO.lunarDate());
        calculateResultBO.setThisYearDate(recordDateBO.solarDate());
        calculateResultBO.setThisYearChineseDate(recordDateBO.lunarDate());
    }

    @Override
    String getEmailMsgContent(EventContextBO contextBO) {
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();

        StringBuilder bf = new StringBuilder();
        bf.append("<p>").append(originalDB.getName()).append("</p>");
        if (calculateResultBO.getIntervalDays() > ZERO_LONG) {
            bf.append(String.format("距离倒计时还有**%d**天！", calculateResultBO.getIntervalDays()));
        } else {
            bf.append("<p>今天就是设定的倒计时哦！</p>");
        }
        if (StringUtils.isNotBlank(originalDB.getTaskDesc())) {
            bf.append("<p>").append(originalDB.getTaskDesc()).append("</p>");
        }
        return bf.toString();
    }

    @Override
    String getDingDingMsgContent(EventContextBO contextBO) {
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();

        List<Object> list = new ArrayList<>();
        list.add(String.format("## %s", originalDB.getName()));
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
        return StringUtils.join(list, "  \n  ");
    }
}
