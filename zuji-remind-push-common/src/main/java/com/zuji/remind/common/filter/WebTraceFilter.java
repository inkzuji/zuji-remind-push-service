package com.zuji.remind.common.filter;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.zuji.remind.common.utils.MdcTraceUtil;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * web请求新增traceId.
 *
 * @author jianjun.wang@theone.art
 * @create  2023-10-18 15:47
 **/
@Component
@Order(1)
@WebFilter("/*")
public class WebTraceFilter extends OncePerRequestFilter {

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
