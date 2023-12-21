package com.zuji.remind.biz.service.db;

import com.zuji.remind.biz.model.bo.MsgPushWayBO;

import java.util.List;

/**
 * 推送方式.
 */
public interface MsgPushWayService {

    List<MsgPushWayBO> listAll();
}
