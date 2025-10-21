package com.ai.qa.user.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 映射数据库中的用户表，存储系统用户的基本信息
 * 使用JPA注解进行对象关系映射(ORM)
 *
 */
@Entity
@Table(name = "user") // 映射数据库表名
@Data // Lombok注解，自动生成getter、setter、toString等方法
public class User {
    /**
     * 用户唯一标识ID
     * 主键，自增长策略，由数据库自动生成
     *
     * @apiNote 示例值: 1, 2, 3...
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     * 用户的登录账号，要求唯一不可重复
     * 长度建议限制，用于身份认证
     *
     * @apiNote 示例值: "admin", "john_doe", "testuser"
     * @constraint 唯一约束，不能为空
     */
    private String username;

    /**
     * 用户昵称
     * 用户的显示名称，可以重复
     * 用于界面展示，增强用户体验
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
     * 用户密码
     * 存储加密后的密码哈希值，不应存储明文密码
     * 使用强加密算法（如BCrypt）进行加密
     *
     * @apiNote 示例值: "$2a$10$abcdefghijklmnopqrstuvwxyz123456"
     * @security 密码长度建议至少6位，包含字母、数字、特殊字符
     */
    private String password;

    /**
     * 创建时间
     * 记录用户账号的创建时间
     * 由系统自动设置，不应手动修改
     *
     * @apiNote 格式: ISO-8601, 示例值: "2024-01-15T10:30:00"
     */
    private LocalDateTime createDate;
    /**
     * 更新时间
     * 记录用户信息的最后修改时间
     * 每次用户信息更新时自动更新为当前时间
     *
     * @apiNote 格式: ISO-8601, 示例值: "2024-01-15T10:30:00"
     */
    private LocalDateTime updateDate;
}
