package com.zuji.remind.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回code.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-22 22:48
 **/
@Getter
@AllArgsConstructor
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    ;

    private final int code;
    private final String message;
}
