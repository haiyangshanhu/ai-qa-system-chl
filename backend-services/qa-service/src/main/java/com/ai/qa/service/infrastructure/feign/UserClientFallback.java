package com.ai.qa.service.infrastructure.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * UserClient的降级处理类
 * 当user-service服务不可用时，自动执行此类中的方法返回降级结果
 * 避免服务雪崩，提高系统容错性
 */
@Slf4j
@Component
public class UserClientFallback implements UserClient {

    /**
     * 获取用户信息的降级处理
     */
    @Override
    public String getUserById(Long userId) {
        log.warn("UserService unavailable, fallback triggered for userId: {}", userId);
        return "{\"error\": \"用户服务暂时不可用\", \"userId\": " + userId + "}";
    }

    /**
     * 获取用户信息的降级处理
     */
    @Override
    public String getUserByUsername(String username) {
        log.warn("UserService unavailable, fallback triggered for username: {}", username);
        return "{\"error\": \"用户服务暂时不可用\", \"userId\": " + username + "}";
    }

    /**
     * 获取用户状态的降级处理
     */
    @Override
    public String getUserStatus(Long userId) {
        log.warn("UserService unavailable, fallback triggered for user status: {}", userId);
        return "{\"status\": \"unknown\", \"userId\": " + userId + "}";
    }

    /**
     * 获取用户基本信息的降级处理
     */
    @Override
    public String getUserBasicInfo(Long userId) {
        log.warn("UserService unavailable, fallback triggered for user basic info: {}", userId);
        return "{\"name\": \"未知用户\", \"userId\": " + userId + "}";
    }
}