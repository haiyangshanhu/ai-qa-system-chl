package com.ai.qa.user.api.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 用户注册响应数据传输对象
 * 继承BaseResponse包含基本响应信息，扩展注册相关的返回数据
 * 用于返回用户注册成功后的用户信息和注册时间
 *
 */
@Data
public class RegisterResponse extends BaseResponse {

    /**
     * 用户ID
     * 系统自动生成的用户唯一标识
     * 注册成功后分配的唯一ID，用于后续业务操作
     *
     * @apiNote 示例值: 10001
     */
    private Long userId;

    /**
     * 用户名
     * 用户注册时设置的登录账号
     * 具有唯一性，不可重复
     *
     * @apiNote 示例值: "john_doe"
     */
    private String username;

    /**
     * 用户昵称
     * 用户注册时设置的显示名称
     * 用于界面展示，可以重复
     *
     * @apiNote 示例值: "John Doe" 或 "小明"
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
     * 注册时间
     * 用户账号创建的成功时间
     * 使用ISO-8601格式的时间戳，记录账号创建的确切时间
     *
     * @apiNote 示例值: "2024-01-15T10:30:00"
     */
    private LocalDateTime registerTime;
}