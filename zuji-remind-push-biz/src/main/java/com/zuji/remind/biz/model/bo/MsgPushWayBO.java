package com.zuji.remind.biz.model.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 消息推送BO.
 */
public class MsgPushWayBO implements Serializable {
    private static final long serialVersionUID = -7117787284498933424L;

    @Data
    public static abstract class WayBO implements Serializable {
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
}
