package com.ai.qa.user.api.dto.request;

import lombok.Data;

/**
 * 用户注册请求数据传输对象
 * 用于接收前端传递的用户注册信息
 * 包含用户基本信息及密码确认字段
 *
 */
@Data
public class RegisterRequest {

    /**
     * 用户名
     * 用户的唯一登录标识，需要保证唯一性
     * 必填字段，长度建议在3-20个字符之间
     *
     * @apiNote 示例值: "john_doe"
     */
    private String username;

    /**
     * 用户昵称
     * 用户的显示名称，可以重复
     * 可选字段，长度建议在2-30个字符之间
     *
     * @apiNote 示例值: "John Doe"
     */
    private String nickname;

    /**
     * 用户邮箱
     * 用户的电子邮箱地址，用于接收通知和重置密码
     * 要求符合邮箱格式规范，通常需要唯一性约束
     * 可用于替代用户名进行登录
     *
     * @apiNote 示例值: "user@example.com", "admin@company.com"
     */
    private String email;

    /**
     * 密码
     * 用户的登录密码，需要进行加密存储
     * 必填字段，建议长度至少6个字符
     *
     * @apiNote 示例值: "Password123"
     * @security 建议包含字母、数字和特殊字符的组合
     */
    private String password;

    /**
     * 确认密码
     * 用于验证用户输入的密码一致性
     * 必填字段，必须与password字段值相同
     *
     * @apiNote 示例值: "Password123"
     * @validation 必须与password字段完全匹配
     */
    private String confirmPassword;
}