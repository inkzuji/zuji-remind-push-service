package com.zuji.remind.biz.service.factory;

import com.zuji.remind.biz.component.datecal.AbstractDateFactory;
import com.zuji.remind.biz.model.bo.EventContextBO;
import com.zuji.remind.biz.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.zuji.remind.common.constant.CommonConstant.ZERO_LONG;

/**
 * 纪念日.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-12 21:41
 **/
@Service
public class AnniversaryEventFactory extends AbstractEventFactory {

    @Override
    protected String getEventTitle() {
        return "纪念日提醒";
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
        bf.append("<p>").append(originalDB.getName()).append("</p>");
        if (calculateResultBO.getIntervalDays() > ZERO_LONG) {
            bf.append("<p>已经").append(calculateResultBO.getRecordDate().until(LocalDate.now(), ChronoUnit.DAYS)).append("天了！</p>");
        } else {
            bf.append("<p>").append(calculateResultBO.getRecordDate().until(LocalDate.now(), ChronoUnit.DAYS)).append("周年快乐！</p>");
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
        list.add(String.format("### %s", originalDB.getName()));
        list.add(String.format("**纪念日**: %s", String.format("%s %s (%s%s)", calculateResultBO.getRecordDate(), DateUtils.week2Str(calculateResultBO.getRecordDate().getDayOfWeek()),
                calculateResultBO.getThisYearChineseDate().getChineseMonthName(), calculateResultBO.getThisYearChineseDate().getChineseDay())));
        if (calculateResultBO.getIntervalDays() > ZERO_LONG) {
            list.add(String.format("已经**%d**天了！", calculateResultBO.getRecordDate().until(LocalDate.now(), ChronoUnit.DAYS)));
        } else {
            list.add(String.format("%d周年快乐！", calculateResultBO.getRecordDate().until(LocalDate.now(), ChronoUnit.YEARS)));
        }
        if (StringUtils.isNotBlank(originalDB.getTaskDesc())) {
            list.add(String.format("> %s", originalDB.getTaskDesc()));
        }
        return StringUtils.join(list, "  \n  ");
    }
}
