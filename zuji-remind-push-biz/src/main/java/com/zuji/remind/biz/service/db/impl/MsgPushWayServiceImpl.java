package com.zuji.remind.biz.service.db.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuji.remind.biz.entity.MsgPushWay;
import com.zuji.remind.biz.mapper.MsgPushWayMapper;
import com.zuji.remind.biz.service.db.MsgPushWayService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 推送方式.
 */
@Service
public class MsgPushWayServiceImpl extends ServiceImpl<MsgPushWayMapper, MsgPushWay> implements MsgPushWayService {

    @Override
    public List<MsgPushWay> listAll() {
        return baseMapper.selectList(Wrappers.emptyWrapper());
    }
}
