package com.ai.qa.user.api.dto.request;

import lombok.Data;

/**
 * 修改密码请求数据传输对象
 * 用于接收前端传递的密码修改信息
 *
 */
@Data
public class UpdatePasswordRequest {

    /**
     * 用户名
     * 当前登录用户的用户名，用于身份验证
     * 必填字段，用于验证用户身份
     *
     * @apiNote 示例值: "john_doe"
     */
    private String username;

    /**
     * 旧密码
     * 用户当前的登录密码，用于身份验证
     * 必填字段，不能为空
     *
     * @apiNote 示例值: "oldPassword123"
     */
    private String oldPassword;

    /**
     * 新密码
     * 用户想要设置的新密码
     * 必填字段，建议长度至少6个字符
     *
     * @apiNote 示例值: "newPassword456"
     * @security 建议包含字母、数字和特殊字符的组合
     */
    private String newPassword;

    /**
     * 确认新密码
     * 用于验证用户输入的新密码一致性
     * 必填字段，必须与newPassword字段值相同
     *
     * @apiNote 示例值: "newPassword456"
     * @validation 必须与newPassword字段完全匹配
     */
    private String confirmNewPassword;
}