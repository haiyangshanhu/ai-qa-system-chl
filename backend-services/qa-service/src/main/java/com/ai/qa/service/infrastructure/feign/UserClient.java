package com.ai.qa.service.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务Feign客户端接口
 * 用于调用user-service微服务的REST API
 *
 * @FeignClient 注解说明：
 *              - name: 指定要调用的服务在注册中心的服务名称
 *              - fallback: 指定服务降级处理类，当服务不可用时执行降级逻辑
 */
@FeignClient(name = "user-service", fallback = UserClientFallback.class)
public interface UserClient {

    /**
     * 根据用户ID获取用户完整信息
     *
     * @param userId 用户ID
     * @return 用户信息的JSON字符串
     */
    @GetMapping("/api/user/{userId}")
    String getUserById(@PathVariable("userId") Long userId);

    /**
     * 根据用户名获取用户完整信息
     *
     * @param username 用户名
     * @return 用户信息的JSON字符串
     */
    @GetMapping("/api/user/username/{username}")
    String getUserByUsername(@PathVariable("username") String username);

    /**
     * 获取用户状态信息
     *
     * @param userId 用户ID
     * @return 用户状态信息的JSON字符串
     */
    @GetMapping("/api/user/{userId}/status")
    String getUserStatus(@PathVariable("userId") Long userId);

    /**
     * 获取用户基本信息
     *
     * @param userId 用户ID
     * @return 用户基本信息的JSON字符串
     */
    @GetMapping("/api/user/{userId}/basic-info")
    String getUserBasicInfo(@PathVariable("userId") Long userId);

}