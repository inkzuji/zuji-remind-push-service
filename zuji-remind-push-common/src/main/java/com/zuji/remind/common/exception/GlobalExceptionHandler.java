package com.zuji.remind.common.exception;

import com.zuji.remind.common.api.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理类.
 *
 * @author inkzuji@gmail.com
 * @create 2023-09-22 23:30
 **/
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理自定义API异常。
     */
    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public CommonResult<Void> handle(ApiException e) {
        log.error(e.getMessage(), e);
        if (e.getErrorCode() != null) {
            return CommonResult.failed(e.getErrorCode(), e.getMessage());
        }
        return CommonResult.failed(e.getMessage());
    }

    /**
     * 处理非法参数异常。
     */
    @ResponseBody
    @ExceptionHandler(value = IllegalArgumentException.class)
    public CommonResult<Void> handle(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return CommonResult.failed(e.getMessage());
    }

    /**
     * 处理请求参数校验异常（@RequestBody 参数）。
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult<Void> handleValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }

    /**
     * 处理请求参数绑定异常。
     */
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public CommonResult<Void> handleValidException(BindException e) {
        log.error(e.getMessage(), e);
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFailed(message);
    }

    /**
     * 处理兜底异常。
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public CommonResult<Void> exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return CommonResult.failed(e.getMessage());
    }
}
