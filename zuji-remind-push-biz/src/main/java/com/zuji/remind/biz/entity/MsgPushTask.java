package com.zuji.remind.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息推送任务.
 *
 * @author inkzuji@gmail.com
 * @since 2023-12-20 17:20
 **/
@Data
@TableName(value = "msg_push_task")
public class MsgPushTask implements Serializable {
    private static final long serialVersionUID = -3456536366528493683L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 推送类型: 1=邮箱;2=钉钉;3=微信
     */
    private Integer msgType;

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
    private Integer status;

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
}