package com.zuji.remind.biz.service.db.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuji.remind.biz.entity.MsgPushTask;
import com.zuji.remind.biz.mapper.MsgPushTaskMapper;
import com.zuji.remind.biz.service.db.MsgPushTaskService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 消息推送任务.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 17:20
 **/
@Service
public class MsgPushTaskServiceImpl extends ServiceImpl<MsgPushTaskMapper, MsgPushTask> implements MsgPushTaskService {

    @Override
    public MsgPushTask getOneById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<MsgPushTask> listBatchByStatus(Collection<Integer> statusList, Long id, Long limit) {
        return baseMapper.selectList(Wrappers.<MsgPushTask>lambdaQuery()
                .in(MsgPushTask::getStatus, statusList)
                .gt(MsgPushTask::getId, id)
                .orderByAsc(MsgPushTask::getId)
                .last(String.format("limit %d", limit)));
    }

    @Override
    public void addOnce(MsgPushTask task) {
        baseMapper.insert(task);
    }

    @Override
    public void addBatch(Collection<MsgPushTask> taskList) {
        super.saveBatch(taskList);
    }

    @Override
    public void updateStatusById(Long id, Integer status, String msgResponse, Integer failNum) {
        baseMapper.update(null, Wrappers.<MsgPushTask>lambdaUpdate()
                .set(Objects.nonNull(status), MsgPushTask::getStatus, status)
                .set(StrUtil.isNotBlank(msgResponse), MsgPushTask::getMsgResponse, msgResponse)
                .set(Objects.nonNull(failNum), MsgPushTask::getFailNum, failNum)
                .eq(MsgPushTask::getId, id));
    }

    @Override
    public void deleteById(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    public void deleteBatch(Collection<Long> ids) {
        baseMapper.delete(Wrappers.<MsgPushTask>lambdaQuery()
                .in(MsgPushTask::getId, ids));
    }
}
