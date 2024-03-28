package com.zuji.remind.biz.scheduler;

import com.zuji.remind.biz.BaseTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * test.
 *
 * @author wangjianjun@c5game.com
 * @since 2024-03-28 11:27
 **/
public class AnniversarySchedulerTest extends BaseTests {

    @Autowired
    private AnniversaryScheduler anniversaryScheduler;

    @Test
    public void testTask() {
        anniversaryScheduler.task();
    }
}
