package com.zuji.remind.biz.component.message;

import com.zuji.remind.biz.enums.RemindWayEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 组件.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 14:15
 **/
@Component
public class MessageNotifyComponent {

    private List<AbstractMessageNotifyFactory> messageNotifyFactoryList;

    private Map<RemindWayEnum, AbstractMessageNotifyFactory> messageNotifyFactoryMap;

    @Autowired
    public void setMessageNotifyFactoryList(List<AbstractMessageNotifyFactory> messageNotifyFactoryList) {
        this.messageNotifyFactoryList = messageNotifyFactoryList;
    }

    /**
     * 初始化消息通知工厂映射，按推送方式枚举值建立索引。
     */
    @PostConstruct
    void init() {
        messageNotifyFactoryMap = messageNotifyFactoryList.stream()
                .collect(Collectors.toMap(AbstractMessageNotifyFactory::getRemindWay, Function.identity()));
    }

    /**
     * 根据推送方式获取对应的消息通知工厂。
     *
     * @param remindWayEnum 推送方式枚举
     * @return 消息通知工厂实例
     */
    public AbstractMessageNotifyFactory getByRemindWay(RemindWayEnum remindWayEnum) {
        return messageNotifyFactoryMap.get(remindWayEnum);
    }
}
