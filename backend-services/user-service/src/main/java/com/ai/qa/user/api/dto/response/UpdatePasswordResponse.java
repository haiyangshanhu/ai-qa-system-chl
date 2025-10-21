package com.ai.qa.user.api.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 修改密码响应数据传输对象
 * 用于返回密码修改操作的结果信息
 *
 */
@Data
public class UpdatePasswordResponse extends BaseResponse {

    /**
     * 用户ID
     * 密码被修改的用户唯一标识
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
     * 密码修改时间
     * 密码成功修改的时间戳
     * 使用ISO-8601格式的时间戳
     *
     * @apiNote 示例值: "2024-01-15T14:30:00"
     */
    private LocalDateTime updateTime;
}