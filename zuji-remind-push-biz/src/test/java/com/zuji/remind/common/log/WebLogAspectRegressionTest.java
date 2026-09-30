package com.zuji.remind.common.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebLogAspectRegressionTest {

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void hostResolutionFailureDoesNotReplaceSuccessfulControllerResult() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
        request.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getMethod()).thenReturn(TestController.class.getMethod("success"));
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        TestController controller = new TestController();
        when(joinPoint.proceed()).thenAnswer(invocation -> controller.success());

        try (MockedStatic<InetAddress> addresses = mockStatic(InetAddress.class)) {
            addresses.when(InetAddress::getLocalHost).thenThrow(new UnknownHostException("test host"));

            assertEquals("success", new WebLogAspect().doAround(joinPoint));
            assertEquals(1, controller.invocations);
            verify(joinPoint, times(1)).proceed();
        }
    }

    @Test
    void controllerExceptionPropagatesWithoutRetry() throws Throwable {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        IllegalStateException failure = new IllegalStateException("controller failed");
        when(joinPoint.proceed()).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalStateException.class, () -> new WebLogAspect().doAround(joinPoint)));
        verify(joinPoint, times(1)).proceed();
    }

    public static class TestController {
        private int invocations;

        public String success() {
            invocations++;
            return "success";
        }
    }
}
