package com.zuji.remind.common.scheduler;

import com.zuji.remind.common.utils.MdcTraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import java.lang.reflect.Method;

/**
 * ScheduledMethodRunnable.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-10-18 15:31
 **/
@Slf4j
public class CommonScheduledMethodRunnable extends ScheduledMethodRunnable {

    public CommonScheduledMethodRunnable(Object target, Method method) {
        super(target, method);
    }

    public CommonScheduledMethodRunnable(Object target, String methodName) throws NoSuchMethodException {
        super(target, methodName);
    }

    @Override
    public void run() {
        // 设置traceId，方便进行日志跟踪
        MdcTraceUtil.addTrace();
        String methodStr = getMethod().toString();
        log.info("{}: start to run", methodStr);
        long startMillis = System.currentTimeMillis();
        try {
            super.run();
        } finally {
            // 监控耗时
            long endMillis = System.currentTimeMillis();
            log.info("{}: cost {} millis", methodStr, endMillis - startMillis);
            MdcTraceUtil.remove();
        }
    }
}
