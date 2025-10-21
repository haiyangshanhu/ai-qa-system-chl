package com.ai.qa.user.api.dto.request;

import lombok.Data;

/**
 * 用户登录请求数据传输对象
 * 用于接收前端传递的用户登录信息
 *
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     * 用户的唯一标识，用于登录认证
     * 必填字段，不能为空
     *
     * @apiNote 示例值: "admin" 或 "testuser"
     */
    private String username;

    /**
     * 密码
     * 用户的登录密码，需要进行加密传输
     * 必填字段，不能为空
     *
     * @apiNote 示例值: "password123"
     * @security 建议在前端进行初步的密码强度验证
     */
    private String password;
}