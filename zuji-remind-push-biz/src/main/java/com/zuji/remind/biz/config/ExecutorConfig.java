package com.zuji.remind.biz.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * ExecutorConfig.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 16:01
 **/
@Data
@Slf4j
@Component
public class ExecutorConfig {
    private static final int DEFAULT_EXECUTOR_CORE_SIZE = 5;
    private static final int DEFAULT_EXECUTOR_MAX_SIZE = 5;
    private static final int DEFAULT_EXECUTOR_ALIVE_SECONDS = 60;
    private static final int DEFAULT_AWAIT_TERMINATION_SECONDS = 60;


    /**
     * 公共线程池配置，核心线程5、最大线程5、队列容量200，拒绝策略为调用者线程执行。
     */
    @Bean("commonThreadPoolExecutor")
    public ThreadPoolTaskExecutor commonThreadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(DEFAULT_EXECUTOR_CORE_SIZE);
        executor.setMaxPoolSize(DEFAULT_EXECUTOR_MAX_SIZE);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(DEFAULT_EXECUTOR_ALIVE_SECONDS);
        executor.setThreadNamePrefix("commonThreadPoolExecutor--");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(DEFAULT_AWAIT_TERMINATION_SECONDS);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
