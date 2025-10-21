package com.ai.qa.user.api.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 用户登录响应数据传输对象
 * 继承BaseResponse包含基本响应信息，扩展登录相关的返回数据
 * 用于返回用户登录成功后的令牌和用户信息
 *
 */
@Data
public class LoginResponse extends BaseResponse {

    /**
     * 访问令牌
     * JWT token，用于后续接口的身份认证和授权
     * 客户端需要在请求头中携带此token: Authorization: Bearer {token}
     *
     * @apiNote 示例值: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * @security 令牌有过期时间，需要定期刷新
     */
    private String token;

    /**
     * 用户ID
     * 用户的唯一标识，系统内部使用
     *
     * @apiNote 示例值: 12345
     */
    private Long userId;

    /**
     * 用户名
     * 用户的登录账号
     *
     * @apiNote 示例值: "john_doe"
     */
    private String username;

    /**
     * 用户昵称
     * 用户的显示名称，用于界面展示
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
     * 登录时间
     * 用户本次登录的成功时间
     * 使用ISO-8601格式的时间戳
     *
     * @apiNote 示例值: "2024-01-15T10:30:00"
     */
    private LocalDateTime loginTime;
}