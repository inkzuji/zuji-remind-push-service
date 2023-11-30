package com.zuji.remind.common.api;

/**
 * api接口返回码.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-22 22:48
 **/
public interface IErrorCode {
    /**
     * 返回码
     */
    int getCode();

    /**
     * 返回信息
     */
    String getMessage();
}
