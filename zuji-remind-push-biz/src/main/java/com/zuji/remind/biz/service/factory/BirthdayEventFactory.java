package com.zuji.remind.biz.service.factory;

import cn.hutool.core.date.ChineseDate;
import com.zuji.remind.biz.component.datecal.AbstractDateFactory;
import com.zuji.remind.biz.model.bo.EventContextBO;
import com.zuji.remind.biz.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
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
    protected String getEventTitle() {
        return "生日提醒";
    }

    @Override
    void calculateDate(EventContextBO contextBO) {
        AbstractDateFactory dateFactory = contextBO.getDateFactory();
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        AbstractDateFactory.DateBO recordDateBO = dateFactory.calculateCurrentDate(originalDB.getMemorialDate(), originalDB.getIsLeapMonth());
        AbstractDateFactory.DateBO nextDateBO = dateFactory.calculateNextDate(originalDB.getMemorialDate(), originalDB.getIsLeapMonth());
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();
        calculateResultBO.setRecordDate(recordDateBO.solarDate());
        calculateResultBO.setRecordChineseDate(recordDateBO.lunarDate());
        calculateResultBO.setThisYearDate(nextDateBO.solarDate());
        calculateResultBO.setThisYearChineseDate(nextDateBO.lunarDate());
    }

    @Override
    String getEmailMsgContent(EventContextBO contextBO) {
        EventContextBO.OriginalDB originalDB = contextBO.getOriginalDB();
        EventContextBO.CalculateResultBO calculateResultBO = contextBO.getCalculateResultBO();

        StringBuilder bf = new StringBuilder();
        bf.append("<p>生日:").append(originalDB.getName()).append("</p>");
        if (calculateResultBO.getIntervalDays() > ZERO_LONG) {
            bf.append(String.format("距离生日还有**%d**天！", calculateResultBO.getIntervalDays()));
        } else {
            bf.append("<p>生日快乐！</p>");
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
        LocalDate thisYearDate = calculateResultBO.getThisYearDate();
        ChineseDate thisYearChineseDate = calculateResultBO.getThisYearChineseDate();
        List<Object> list = new ArrayList<>();
        list.add(String.format("### %s", originalDB.getName()));
        list.add(String.format("**生日**: %s", String.format("%s %s (%s%s)", thisYearDate, DateUtils.week2Str(thisYearDate.getDayOfWeek()),
                thisYearChineseDate.getChineseMonthName(), thisYearChineseDate.getChineseDay())));
        if (calculateResultBO.getIntervalDays() > ZERO_LONG) {
            list.add(String.format("距离生日还有**%d**天！", calculateResultBO.getIntervalDays()));
        } else {
            list.add("生日快乐！");
        }
        if (StringUtils.isNotBlank(originalDB.getTaskDesc())) {
            list.add(String.format("> %s  \n", originalDB.getTaskDesc()));
        }
        return StringUtils.join(list, "  \n  ");
    }
}
