package com.zuji.remind.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
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
    public ListenableFuture<?> submitListenable(Runnable task) {
        showThreadPoolInfo("1. do submitListenable");
        return super.submitListenable(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        showThreadPoolInfo("2. do submitListenable");
        return super.submitListenable(task);
    }
}
