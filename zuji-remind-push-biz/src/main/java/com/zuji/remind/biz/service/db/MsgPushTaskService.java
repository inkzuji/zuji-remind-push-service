package com.zuji.remind.biz.service.db;

import com.zuji.remind.biz.entity.MsgPushTask;
import com.zuji.remind.biz.model.bo.MsgPushTaskBO;

import java.util.Collection;
import java.util.List;

/**
 * 消息推送任务.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 17:20
 **/
public interface MsgPushTaskService {

    MsgPushTask getOneById(Long id);

    List<MsgPushTaskBO> listBatchByStatus(Collection<Integer> statusList, Long id, Long limit);

    List<MsgPushTask> listBatchByMsgIndex(Integer msgIndex, Long id, Long limit);

    void addOnce(MsgPushTask task);

    void addBatch(Collection<MsgPushTask> taskList);

    void updateStatusById(Long id, Integer status, String msgResponse, Integer failNum);

    void deleteById(Long id);

    void deleteBatch(Collection<Long> ids);
}
