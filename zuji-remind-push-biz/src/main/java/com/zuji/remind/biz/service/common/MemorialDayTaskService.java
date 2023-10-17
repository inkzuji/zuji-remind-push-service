package com.zuji.remind.biz.service.common;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zuji.remind.biz.entity.MemorialDayTask;

import java.util.List;

/**
 * 倒数记录任务.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-11 22:07
 **/
public interface MemorialDayTaskService extends IService<MemorialDayTask> {
    List<MemorialDayTask> listAll();
}
