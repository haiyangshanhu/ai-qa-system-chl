package com.ai.qa.user.common;

import java.util.Base64;

import io.jsonwebtoken.Jwts;

/**
 * JWT密钥生成工具类
 * 用于生成安全的HS512算法密钥，适用于JWT令牌签名
 * 生成的密钥为Base64编码格式，可直接配置到application.yml中
 *
 */
public class KeyGenerator {
    /**
     * 主方法 - 生成HS512 JWT签名密钥
     * 生成512位（64字节）的HMAC-SHA512密钥，并输出Base64编码结果
     *
     * @param args 命令行参数（未使用）
     *
     * @apiNote 运行方法: java KeyGenerator
     * @apiNote 输出示例: MzVkMjBjZDctYjUzNi00ZmUyLWEwODAtNGQ5OTZhYzE2M2JhYjY0ZWM0NDctNTIzNS00ZDkyLWExMjMtZjA1M2Q4ZGUxNzRk
     *
     * @security 注意:
     * 1. 生成的密钥应妥善保管，不要泄露
     * 2. 生产环境建议使用密钥管理服务（如KMS、HashiCorp Vault等）
     * 3. 定期轮换密钥以增强安全性
     * 4. 不同环境（开发、测试、生产）应使用不同的密钥
     */
    public static void main(String[] args) {
        // 使用JJWT 0.12.x版本推荐的方式生成HS512密钥
        byte[] keyBytes = Jwts.SIG.HS512.key().build().getEncoded();
        // 将二进制密钥转换为Base64编码字符串，便于配置文件使用
        // Base64编码使二进制数据可以文本形式安全存储
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("生成的512位密钥（请复制到application.yml）：");
        System.out.println("==================================================");
        System.out.println(base64Key);
        System.out.println("==================================================");
    }
}
