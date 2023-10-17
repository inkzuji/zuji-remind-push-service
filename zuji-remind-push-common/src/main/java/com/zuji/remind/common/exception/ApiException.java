package com.zuji.remind.common.exception;

import com.zuji.remind.common.api.IErrorCode;

/**
 * 自定义异常.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-22 23:29
 **/
public class ApiException extends RuntimeException {
    private static final long serialVersionUID = -1810598636300210384L;
    private IErrorCode errorCode;

    public ApiException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}
