package com.zuji.remind.common.filter;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.zuji.remind.common.utils.MdcTraceUtil;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * web请求新增traceId.
 *
 * @author inkzuji@gmail.com
 * @create  2023-10-18 15:47
 **/
@Component
@Order(1)
@WebFilter("/*")
public class WebTraceFilter extends OncePerRequestFilter {

    /**
     * 为每个请求注入 traceId，从请求头读取或自动生成，请求结束后清理。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = request.getHeader(MdcTraceUtil.TRACE_ID);
            MdcTraceUtil.putTrace(StrUtil.blankToDefault(traceId, IdUtil.fastSimpleUUID()));
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
