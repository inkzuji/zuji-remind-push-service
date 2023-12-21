package com.zuji.remind.biz.model.bo;

import com.zuji.remind.biz.entity.MsgPushTask;
import com.zuji.remind.biz.enums.RemindWayEnum;
import com.zuji.remind.biz.enums.TaskStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息推送任务BO.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-21 14:22
 **/
@Data
public class MsgPushTaskBO implements Serializable {
    private static final long serialVersionUID = -5730366642572284320L;

    private Long id;

    /**
     * 推送类型: 1=邮箱;2=钉钉;3=微信
     */
    private RemindWayEnum msgType;

    /**
     * 消息内容
     */
    private String msgRequest;

    /**
     * 消息发送结果
     */
    private String msgResponse;

    /**
     * 发送状态：0=待发送，1=发送中，2=已发送，3=发送失败,待重试，4=发送失败
     */
    private TaskStatusEnum status;

    /**
     * 推送次数
     */
    private Integer failNum;

    /**
     * 消息标记点位，用于定时删除
     */
    private Integer msgIndex;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public static MsgPushTaskBO from(MsgPushTask task) {
        MsgPushTaskBO bo = new MsgPushTaskBO();
        bo.setId(task.getId());
        bo.setMsgType(RemindWayEnum.getByCode(task.getMsgType()));
        bo.setMsgRequest(task.getMsgRequest());
        bo.setMsgResponse(task.getMsgResponse());
        bo.setStatus(TaskStatusEnum.getByCode(task.getStatus()));
        bo.setFailNum(task.getFailNum());
        bo.setMsgIndex(task.getMsgIndex());
        bo.setUpdateTime(task.getUpdateTime());
        bo.setCreateTime(task.getCreateTime());
        return bo;
    }
}
