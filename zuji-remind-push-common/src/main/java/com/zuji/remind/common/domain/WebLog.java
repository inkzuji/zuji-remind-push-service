package com.zuji.remind.common.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Controller层的日志封装类.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-23 16:01
 **/
public class WebLog implements Serializable {
    private static final long serialVersionUID = -8789027822774189437L;
    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作用户
     */
    private String username;

    /**
     * 操作时间
     */
    private Long startTime;

    /**
     * 消耗时间
     */
    private Integer spendTime;

    /**
     * 根路径
     */
    private String basePath;

    /**
     * URI
     */
    private String uri;

    /**
     * URL
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 请求参数
     */
    private Object parameter;

    /**
     * 返回结果
     */
    private Object result;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Integer getSpendTime() {
        return this.spendTime;
    }

    public void setSpendTime(Integer spendTime) {
        this.spendTime = spendTime;
    }

    public String getBasePath() {
        return this.basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Object getParameter() {
        return this.parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebLog webLog = (WebLog) o;
        return Objects.equals(description, webLog.description) && Objects.equals(username, webLog.username) && Objects.equals(startTime, webLog.startTime) && Objects.equals(spendTime, webLog.spendTime) && Objects.equals(basePath, webLog.basePath) && Objects.equals(uri, webLog.uri) && Objects.equals(url, webLog.url) && Objects.equals(method, webLog.method) && Objects.equals(ip, webLog.ip) && Objects.equals(parameter, webLog.parameter) && Objects.equals(result, webLog.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, username, startTime, spendTime, basePath, uri, url, method, ip, parameter, result);
    }

    @Override
    public String toString() {
        return "WebLog{" +
                "description='" + description + '\'' +
                ", username='" + username + '\'' +
                ", startTime=" + startTime +
                ", spendTime=" + spendTime +
                ", basePath='" + basePath + '\'' +
                ", uri='" + uri + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", ip='" + ip + '\'' +
                ", parameter=" + parameter +
                ", result=" + result +
                '}';
    }
}
