package com.zuji.remind.biz.service.db;

import com.zuji.remind.biz.entity.MemorialDayTask;

import java.util.List;

/**
 * 倒数记录任务.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-11 22:07
 **/
public interface MemorialDayTaskService {
    List<MemorialDayTask> listAll();

    MemorialDayTask getById(Long id);

    boolean insertOnce(MemorialDayTask task);

    boolean updateById(Long id, MemorialDayTask task);

    boolean deleteById(Long id);
}
