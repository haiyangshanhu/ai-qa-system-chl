package com.ai.qa.service.infrastructure.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign客户端配置类
 * 用于配置Feign客户端的全局行为
 */
@Configuration
public class FeignConfig {

    /**
     * 配置Feign客户端的日志级别
     * FULL级别会记录请求和响应的头信息、正文和元数据
     *
     * @return Logger.Level.FULL 最详细的日志级别
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}