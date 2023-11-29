package com.zuji.remind.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-12 22:10
 **/
@Configuration
@ConfigurationProperties(prefix = "custom")
@Data
public class CommonConfig {

}
