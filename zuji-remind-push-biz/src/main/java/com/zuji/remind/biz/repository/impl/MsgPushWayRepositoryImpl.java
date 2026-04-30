package com.zuji.remind.biz.repository.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuji.remind.biz.dao.entity.MsgPushWay;
import com.zuji.remind.biz.dao.mapper.MsgPushWayMapper;
import com.zuji.remind.biz.model.bo.MsgPushWayBO;
import com.zuji.remind.biz.repository.MsgPushWayRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推送方式.
 */
@Repository
public class MsgPushWayRepositoryImpl extends ServiceImpl<MsgPushWayMapper, MsgPushWay> implements MsgPushWayRepository {

    @Override
    public List<MsgPushWayBO> listAll() {
        List<MsgPushWay> list = baseMapper.selectList(Wrappers.emptyWrapper());
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(MsgPushWayBO::from)
                .collect(Collectors.toList());
    }
}
