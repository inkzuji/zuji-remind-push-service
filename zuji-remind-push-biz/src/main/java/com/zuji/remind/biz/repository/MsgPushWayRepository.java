package com.zuji.remind.biz.repository;

import com.zuji.remind.biz.model.bo.MsgPushWayBO;

import java.util.List;

/**
 * 推送方式.
 */
public interface MsgPushWayRepository {

    /**
     * 查询全部推送渠道配置。
     *
     * @return 推送渠道配置列表
     */
    List<MsgPushWayBO> listAll();
}
