package com.zuji.remind.common.constant;

import com.zuji.remind.common.scheduler.CommonScheduledAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

/**
 * SchedulingConfiguration.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-10-18 15:33
 **/
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SchedulingConfiguration {

    @Bean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CommonScheduledAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
        return new CommonScheduledAnnotationBeanPostProcessor();
    }
}
