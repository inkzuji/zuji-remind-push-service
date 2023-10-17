package com.zuji.remind.biz.service.common.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuji.remind.biz.entity.MemorialDayTask;
import com.zuji.remind.biz.mapper.MemorialDayTaskMapper;
import com.zuji.remind.biz.service.common.MemorialDayTaskService;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
