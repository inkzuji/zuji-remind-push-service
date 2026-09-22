package com.zuji.remind.biz.client;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DingDingPushClientRegressionTest {

    @Test
    void generatingSignatureDoesNotLogCredentials() {
        Logger logger = (Logger) LoggerFactory.getLogger(DingDingPushClient.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            String secret = "regression-test-signing-secret";
            String sign = ReflectionTestUtils.invokeMethod(new DingDingPushClient(), "generateSign", 1700000000000L, secret);

            assertNotNull(sign);
            assertFalse(sign.isBlank());
            assertFalse(appender.list.stream().map(ILoggingEvent::getFormattedMessage)
                    .anyMatch(message -> message.contains(secret) || message.contains(sign)));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }
}
