package com.zuji.remind.biz.repository;

import com.zuji.remind.biz.dao.entity.MemorialDayTask;

import java.util.List;

/**
 * 倒数记录任务.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-11 22:07
 **/
public interface MemorialDayTaskRepository {

    /**
     * 查询全部纪念日任务。
     *
     * @return 纪念日任务列表
     */
    List<MemorialDayTask> listAll();

    /**
     * 根据ID查询纪念日任务。
     *
     * @param id 任务ID
     * @return 纪念日任务实体
     */
    MemorialDayTask getById(Long id);

    /**
     * 新增一条纪念日任务。
     *
     * @param task 纪念日任务实体
     * @return 是否新增成功
     */
    boolean insertOnce(MemorialDayTask task);

    /**
     * 根据ID更新纪念日任务。
     *
     * @param id   任务ID
     * @param task 更新内容
     * @return 是否更新成功
     */
    boolean updateById(Long id, MemorialDayTask task);

    /**
     * 根据ID删除纪念日任务。
     *
     * @param id 任务ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);
}
