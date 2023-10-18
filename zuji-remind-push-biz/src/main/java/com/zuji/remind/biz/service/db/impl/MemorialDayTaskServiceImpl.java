package com.zuji.remind.biz.service.db.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuji.remind.biz.entity.MemorialDayTask;
import com.zuji.remind.biz.mapper.MemorialDayTaskMapper;
import com.zuji.remind.biz.service.db.MemorialDayTaskService;
import com.zuji.remind.common.exception.Asserts;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 倒数记录任务.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-11 22:07
 **/
@Service
public class MemorialDayTaskServiceImpl extends ServiceImpl<MemorialDayTaskMapper, MemorialDayTask> implements MemorialDayTaskService {

    @Override
    public List<MemorialDayTask> listAll() {
        return baseMapper.selectList(Wrappers.emptyWrapper());
    }

    @Override
    public MemorialDayTask getById(Long id) {
        if (Objects.isNull(id)) {
            Asserts.fail("参数`id`不能未null");
        }
        return baseMapper.selectOne(Wrappers.<MemorialDayTask>lambdaQuery()
                .eq(MemorialDayTask::getId, id));
    }

    @Override
    public boolean insertOnce(MemorialDayTask task) {
        if (Objects.isNull(task)) {
            Asserts.fail("参数`task`不能未null");
        }
        int result = baseMapper.insert(task);
        return 1 == result;
    }

    @Override
    public boolean updateById(Long id, MemorialDayTask task) {
        if (Objects.isNull(id)) {
            Asserts.fail("参数`id`不能未null");
        }
        if (Objects.isNull(task)) {
            Asserts.fail("参数`task`不能未null");
        }
        int result = baseMapper.update(task, Wrappers.<MemorialDayTask>lambdaUpdate()
                .eq(MemorialDayTask::getId, id));
        return 1 == result;
    }

    @Override
    public boolean deleteById(Long id) {
        if (Objects.isNull(id)) {
            Asserts.fail("参数`id`不能未null");
        }
        int result = baseMapper.deleteById(id);
        return 1 == result;
    }
}
