package com.zuji.remind.common.exception;

import com.zuji.remind.common.api.IErrorCode;

/**
 * 断言处理类，用于抛出各种API异常.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-22 23:29
 **/
public class Asserts {
    /**
     * 抛出自定义API异常。
     *
     * @param message 错误信息
     */
    public static void fail(String message) {
        throw new ApiException(message);
    }

    /**
     * 抛出自定义API异常。
     *
     * @param errorCode 错误码枚举
     */
    public static void fail(IErrorCode errorCode) {
        throw new ApiException(errorCode);
    }
}
