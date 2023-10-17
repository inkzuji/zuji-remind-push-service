package com.zuji.remind.biz.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mybatis配置.
 *
 * @author jianjun.wang@theone.art
 * @create 2023-09-23 16:16
 **/
@Configuration
@EnableTransactionManagement
@MapperScan("com.zuji.remind.biz.mapper")
public class MybatisConfig {
}
