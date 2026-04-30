package com.zuji.remind.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * VisibleThreadPoolTaskExecutor.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 15:26
 **/
@Slf4j
public class VisibleThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    private static final long serialVersionUID = -9072805074040765697L;

    public VisibleThreadPoolTaskExecutor() {
        super();
        setTaskDecorator(new MdcTaskDecorator());
    }


    /**
     * 打印线程池运行状态信息。
     *
     * @param prefix 操作前缀描述
     */
    private void showThreadPoolInfo(String prefix) {
        ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();
        log.info("{}, {}, 当前已提交数量[{}], 当前已完成数量[{}], 当前线程数量[{}], 当前队列数量[{}]",
                this.getThreadNamePrefix()
                , prefix
                , threadPoolExecutor.getTaskCount()
                , threadPoolExecutor.getCompletedTaskCount()
                , threadPoolExecutor.getActiveCount()
                , threadPoolExecutor.getQueue().size());
    }

    @Override
    public void execute(Runnable task) {
        showThreadPoolInfo("1. do execute");
        super.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        showThreadPoolInfo("2. do execute");
        super.execute(task, startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        showThreadPoolInfo("1. do submit");
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        showThreadPoolInfo("2. do submit");
        return super.submit(task);
    }

    @Override
    public CompletableFuture<Void> submitCompletable(Runnable task) {
        showThreadPoolInfo("1. do submitCompletable");
        return super.submitCompletable(task);
    }

    @Override
    public <T> CompletableFuture<T> submitCompletable(Callable<T> task) {
        showThreadPoolInfo("2. do submitCompletable");
        return super.submitCompletable(task);
    }
}
