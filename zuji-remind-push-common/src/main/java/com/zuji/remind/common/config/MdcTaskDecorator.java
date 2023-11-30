package com.zuji.remind.common.config;

import com.zuji.remind.common.utils.MdcTraceUtil;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * MdcTaskDecorator.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 15:17
 **/
public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> map = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (!CollectionUtils.isEmpty(map)) {
                    MDC.setContextMap(map);
                }
                if (!StringUtils.hasText(MdcTraceUtil.getTraceId())) {
                    MdcTraceUtil.addTrace();
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
