package com.zuji.remind.biz.service.db.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuji.remind.biz.entity.MsgPushWay;
import com.zuji.remind.biz.mapper.MsgPushWayMapper;
import com.zuji.remind.biz.model.bo.MsgPushWayBO;
import com.zuji.remind.biz.service.db.MsgPushWayService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推送方式.
 */
@Service
public class MsgPushWayServiceImpl extends ServiceImpl<MsgPushWayMapper, MsgPushWay> implements MsgPushWayService {

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
