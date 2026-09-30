package com.zuji.remind.common.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class RequestUtilRegressionTest {

    @ParameterizedTest
    @ValueSource(strings = {"127.0.0.1", "0:0:0:0:0:0:0:1"})
    void hostResolutionFailurePreservesRemoteAddressAndLogsWarning(String remoteAddress) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddress);
        Logger logger = (Logger) LoggerFactory.getLogger(RequestUtil.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try (MockedStatic<InetAddress> addresses = mockStatic(InetAddress.class)) {
            addresses.when(InetAddress::getLocalHost).thenThrow(new UnknownHostException("test host"));

            assertEquals(remoteAddress, RequestUtil.getRequestIp(request));
            assertTrue(appender.list.stream().anyMatch(event -> event.getLevel() == Level.WARN
                    && event.getFormattedMessage().contains(remoteAddress)
                    && event.getThrowableProxy() != null
                    && event.getThrowableProxy().getClassName().equals(UnknownHostException.class.getName())));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void localRequestUsesResolvedHostAddress() throws UnknownHostException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        InetAddress localAddress = InetAddress.getByAddress(new byte[]{10, 0, 0, 8});
        try (MockedStatic<InetAddress> addresses = mockStatic(InetAddress.class)) {
            addresses.when(InetAddress::getLocalHost).thenReturn(localAddress);

            assertEquals("10.0.0.8", RequestUtil.getRequestIp(request));
        }
    }

    @Test
    void directRequestKeepsRemoteAddress() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.0.2.42");

        assertEquals("192.0.2.42", RequestUtil.getRequestIp(request));
    }

    @Test
    void forwardedRequestUsesFirstProxyAddress() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("x-forwarded-for", "198.51.100.42, 192.0.2.42");

        assertEquals("198.51.100.42", RequestUtil.getRequestIp(request));
    }
}
