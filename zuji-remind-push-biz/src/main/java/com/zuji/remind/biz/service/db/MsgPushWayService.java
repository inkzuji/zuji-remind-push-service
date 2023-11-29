package com.zuji.remind.biz.service.db;

import com.zuji.remind.biz.entity.MsgPushWay;

import java.util.List;

/**
 * 推送方式.
 */
public interface MsgPushWayService {

    List<MsgPushWay> listAll();
}
