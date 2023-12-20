package com.zuji.remind.biz.model.bo;

import cn.hutool.json.JSONUtil;
import com.zuji.remind.biz.entity.MsgPushWay;
import com.zuji.remind.biz.enums.RemindWayEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 消息推送BO.
 */
@Data
public class MsgPushWayBO implements Serializable {
    private static final long serialVersionUID = -7117787284498933424L;

    /**
     * 推送类型: 1=邮箱;2=钉钉;3=微信
     */
    private RemindWayEnum pushType;

    /**
     * 名称
     */
    private String name;

    /**
     * 推送内容参数
     */
    private MsgPushWayBO.WayBO pushRequestParam;

    @Data
    public abstract static class WayBO implements Serializable {
        private static final long serialVersionUID = 8064145450001775052L;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class EmailWayBO extends WayBO {
        private static final long serialVersionUID = 5870942945199307763L;
        /**
         * 收件人。
         */
        private String[] to;

        /**
         * 抄送人。
         */
        private String[] cc;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DingDingBO extends WayBO {
        private static final long serialVersionUID = 3535657997739696613L;
        private String url;
        private String accessToken;
        private String secret;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class WechatBO extends WayBO {

        private static final long serialVersionUID = 435055194309271393L;
    }

    public static MsgPushWayBO from(MsgPushWay way) {
        RemindWayEnum remindWayEnum = RemindWayEnum.getByCode(way.getPushType());
        MsgPushWayBO wayBO = new MsgPushWayBO();
        wayBO.setPushType(remindWayEnum);
        wayBO.setName(way.getName());
        wayBO.setPushRequestParam(convert(remindWayEnum, way.getPushContext()));
        return wayBO;
    }

    public static WayBO convert(RemindWayEnum remindWayEnum, String context) {
        switch (remindWayEnum) {
            case EMAIL:
                return JSONUtil.toBean(context, EmailWayBO.class);
            case DING_DING:
                return JSONUtil.toBean(context, DingDingBO.class);
            case WECHAT:
                return JSONUtil.toBean(context, WechatBO.class);
            default:
                throw new RuntimeException("暂不支持该类型");
        }
    }
}
