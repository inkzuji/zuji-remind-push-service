package com.zuji.remind.biz.client;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.zuji.remind.biz.config.CommonConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 钉钉推送.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-12 22:06
 **/
@Component
@Slf4j
public class DingDingPushClient {
    private static final String DEFAULT_ALGORITHM = "HmacSHA256";

    private final CommonConfig.DingDingConfig config;

    public DingDingPushClient(CommonConfig config) {
        this.config = config.getDingDingConfig();
    }

    /**
     * 推送消息。
     *
     * @param requestBody 内容
     */
    public void send(OapiRobotSendRequest requestBody) {
        String url;
        if (StringUtils.isBlank(config.getSecret())) {
            url = String.format("%s%s", config.getUrl(), config.getAccessToken());
        } else {
            long timestamp = System.currentTimeMillis();
            String sign = this.generateSign(timestamp, config.getSecret());
            url = String.format("%s%s&timestamp=%d&sign=%s", config.getUrl(), config.getAccessToken(), timestamp, sign);
        }
        try {
            DingTalkClient client = new DefaultDingTalkClient(url);
            OapiRobotSendResponse response = client.execute(requestBody);
            log.info("推送钉钉消息, response={}", response.getBody());
            if (!response.isSuccess()) {
                throw new RuntimeException("推送钉钉消息失败, errMsg=" + response.getErrmsg());
            }
            log.info("推送钉钉消息, response={}", response.getBody());
        } catch (Exception e) {
            log.error("推送钉钉消息失败, errMsg={}", e.getMessage(), e);
        }
    }

    /**
     * 生成签名.
     *
     * @param timestamp 时间戳
     * @param secret    签名密钥
     * @return
     */
    private String generateSign(Long timestamp, String secret) {
        String toSign = String.format("%d%n%s", timestamp, secret);
        try {
            Mac instance = Mac.getInstance(DEFAULT_ALGORITHM);
            instance.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), DEFAULT_ALGORITHM));
            byte[] signData = instance.doFinal(toSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            log.info("生成签名: timestamp={},secret={},sign={}", timestamp, secret, sign);
            return sign;
        } catch (Exception e) {
            return null;
        }
    }
}
