package com.zuji.remind.common.scheduler;

import org.springframework.aop.support.AopUtils;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * ScheduledAnnotationBeanPostProcessor.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 15:32
 **/
public class CommonScheduledAnnotationBeanPostProcessor extends ScheduledAnnotationBeanPostProcessor {

    @Override
    protected Runnable createRunnable(Object target, Method method) {
        // 这两行代码从ScheduledAnnotationBeanPostProcessor#createRunnable复制过来
        Assert.isTrue(method.getParameterCount() == 0, "Only no-arg methods may be annotated with @Scheduled");
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());

        // 把ScheduledMethodRunnable替换为CommonScheduledMethodRunnable
        return new CommonScheduledMethodRunnable(target, invocableMethod);
    }
}
