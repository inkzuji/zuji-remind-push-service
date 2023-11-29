package com.zuji.remind.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 推送方式
 */
@Data
@TableName(value = "msg_push_way")
public class MsgPushWay implements Serializable {
    private static final long serialVersionUID = 6254623331722133807L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 推送类型: 1=邮箱;2=钉钉;3=微信
     */
    private int pushType;

    /**
     * 名称
     */
    private String name;

    /**
     * 推送内容参数
     */
    private String pushContext;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否删除：0=否，1=是
     */
    @TableLogic(value = "0", delval = "1")
    private int isDelete;
}