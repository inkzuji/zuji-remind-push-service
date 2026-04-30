package com.zuji.remind.biz.repository;

import com.zuji.remind.biz.dao.entity.MsgPushTask;
import com.zuji.remind.biz.model.bo.MsgPushTaskBO;

import java.util.Collection;
import java.util.List;

/**
 * 消息推送任务.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 17:20
 **/
public interface MsgPushTaskRepository {

    /**
     * 根据ID查询单条推送任务。
     *
     * @param id 任务ID
     * @return 推送任务实体
     */
    MsgPushTask getOneById(Long id);

    /**
     * 按状态批量查询推送任务（支持游标分页）。
     *
     * @param statusList 状态列表
     * @param id         起始ID（游标），为null时不限制
     * @param limit      查询条数
     * @return 推送任务业务对象列表
     */
    List<MsgPushTaskBO> listBatchByStatus(Collection<Integer> statusList, Long id, Long limit);

    /**
     * 按消息索引批量查询推送任务（支持游标分页）。
     *
     * @param msgIndex 消息索引（纯日期格式，如20231220）
     * @param id       起始ID（游标），为null时不限制
     * @param limit    查询条数
     * @return 推送任务实体列表
     */
    List<MsgPushTask> listBatchByMsgIndex(Integer msgIndex, Long id, Long limit);

    /**
     * 新增单条推送任务。
     *
     * @param task 推送任务实体
     */
    void addOnce(MsgPushTask task);

    /**
     * 批量新增推送任务。
     *
     * @param taskList 推送任务实体集合
     */
    void addBatch(Collection<MsgPushTask> taskList);

    /**
     * 根据ID更新推送任务状态。
     *
     * @param id          任务ID
     * @param status      目标状态
     * @param msgResponse 推送响应内容
     * @param failNum     失败次数
     */
    void updateStatusById(Long id, Integer status, String msgResponse, Integer failNum);

    /**
     * 根据ID删除推送任务。
     *
     * @param id 任务ID
     */
    void deleteById(Long id);

    /**
     * 批量删除推送任务。
     *
     * @param ids 任务ID集合
     */
    void deleteBatch(Collection<Long> ids);
}
