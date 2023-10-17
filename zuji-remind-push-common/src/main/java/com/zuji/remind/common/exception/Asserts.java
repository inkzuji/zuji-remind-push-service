package com.zuji.remind.common.exception;

import com.zuji.remind.common.api.IErrorCode;

/**
 * 断言处理类，用于抛出各种API异常.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-22 23:29
 **/
public class Asserts {
    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(IErrorCode errorCode) {
        throw new ApiException(errorCode);
    }
}
