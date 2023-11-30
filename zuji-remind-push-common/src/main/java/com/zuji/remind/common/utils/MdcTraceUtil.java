package com.zuji.remind.common.utils;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

/**
 * mdc tranceId 工具类.
 *
 * @author inkzuji@gmail.com
 * @create 2023-10-18 15:51
 **/
public class MdcTraceUtil {
    private MdcTraceUtil() {
    }
    public static final String TRACE_ID = "traceId";

    /**
     * 新增。
     */
    public static void addTrace() {
        MDC.put(TRACE_ID, IdUtil.fastSimpleUUID());
    }

    /**
     * 指定赋值traceId.
     */
    public static void putTrace(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }

    /**
     * 获取traceId.
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 清理traceId.
     */
    public static void remove() {
        MDC.remove(TRACE_ID);
    }
}
