package com.ai.qa.user.common;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

/**
 * JWT工具类
 * 负责JWT令牌的生成、解析和验证
 * 使用JJWT库处理JSON Web Tokens，支持HS512签名算法
 * 提供完整的JWT生命周期管理功能
 *
 */
@Component
public class JwtUtil {

    /**
     * JWT签名密钥
     * 从配置文件中注入，需要Base64编码的字符串
     * 建议使用至少256位的密钥长度以确保安全性
     *
     * @apiNote 在application.yml中配置: jwt.secret=your-base64-encoded-secret
     * @security 生产环境应使用安全的密钥管理方式
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT过期时间（毫秒）
     * 从配置文件中注入，表示令牌的有效期
     * 根据安全要求调整过期时间，平衡安全性和用户体验
     *
     * @apiNote 示例值:
     * - 3600000: 1小时 (60*60*1000)
     * - 86400000: 24小时 (24*60*60*1000)
     * - 604800000: 7天 (7*24*60*60*1000)
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * JWT声明中用户名的键名常量
     * 用于统一管理JWT payload中的字段名称
     * 避免硬编码，提高代码可维护性
     */
    private static final String CLAIM_KEY_USERNAME = "username";

    /**
     * 获取符合HMAC-SHA512要求的签名密钥
     * 将Base64编码的密钥字符串转换为JJWT所需的SecretKey对象
     * 使用Keys.hmacShaKeyFor()方法确保密钥长度符合HS512算法要求
     *
     * @return SecretKey 用于JWT签名验证的密钥对象
     * @throws IllegalArgumentException 当密钥Base64解码失败或长度不符合要求时抛出
     * @security 确保密钥长度至少256位（32字节）
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT令牌
     * 根据用户名创建包含基本声明的JWT令牌，使用HS512算法进行签名
     * 包含标准声明（iat, exp）和自定义声明（username）
     *
     * @param username 用户名，将作为自定义声明添加到令牌payload中
     * @return String 生成的JWT令牌字符串，格式: header.payload.signature
     * @throws IllegalArgumentException 当用户名为空或密钥无效时抛出
     * @apiNote 生成的令牌示例: eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...
     */
    public String generateToken(String username) {
        // 参数验证
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        // 创建自定义声明（Claims）
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, username);

        // 构建JWT令牌（使用JJWT 0.12.x版本的流式API）
        return Jwts.builder()
                .claims(claims)                  // 设置自定义声明
                .issuedAt(now)                   // 设置签发时间（iat）
                .expiration(expirationDate)      // 设置过期时间（exp）
                .signWith(getSigningKey(), Jwts.SIG.HS512)  // 使用HS512算法签名
                .compact();                      // 生成最终的令牌字符串
    }

    /**
     * 从JWT令牌中提取用户名
     * 解析并验证令牌签名，然后从payload中获取用户名声明
     *
     * @param token JWT令牌字符串
     * @return String 用户名
     * @throws ExpiredJwtException 当令牌已过期时抛出
     * @throws UnsupportedJwtException 当令牌格式不支持时抛出
     * @throws MalformedJwtException 当令牌格式错误时抛出
     * @throws SignatureException 当签名验证失败时抛出
     * @throws IllegalArgumentException 当令牌为空或格式错误时抛出
     * @security 此方法会验证令牌签名，确保令牌未被篡改
     */
    public String getUsernameFromToken(String token) {
        // 参数基础验证
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        // 创建JWT解析器并设置验证密钥
        JwtParser parser = Jwts.parser()
                .verifyWith(getSigningKey())      // 设置验证密钥
                .build();

        // 解析并验证令牌签名
        Jws<Claims> jws = parser.parseSignedClaims(token);

        // 从payload中获取username声明
        return jws.getPayload().get(CLAIM_KEY_USERNAME, String.class);
    }

    /**
     * 验证JWT令牌的有效性
     * 综合检查令牌的签名、过期时间、格式等是否有效
     *
     * @param token JWT令牌字符串
     * @return boolean true表示令牌有效且未过期，false表示无效或已过期
     * @apiNote 此方法捕获所有异常并返回false，适合用于快速验证
     * @apiNote 如果需要详细的错误信息，请使用getUsernameFromToken方法
     */
    public boolean validateToken(String token) {
        try {
            // 基础空值检查
            if (token == null || token.trim().isEmpty()) {
                return false;
            }

            JwtParser parser = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build();

            // 解析并验证令牌，失败会抛出相应异常
            parser.parseSignedClaims(token);
            return true;

        } catch (ExpiredJwtException e) {
            // 令牌已过期
            return false;
        } catch (UnsupportedJwtException e) {
            // 不支持的JWT格式
            return false;
        } catch (MalformedJwtException e) {
            // 令牌格式错误
            return false;
        } catch (SignatureException e) {
            // 签名验证失败（可能被篡改）
            return false;
        } catch (IllegalArgumentException e) {
            // 参数错误（如空令牌）
            return false;
        } catch (JwtException e) {
            // 其他JWT相关异常
            return false;
        }
    }

    /**
     * 获取令牌剩余有效时间（毫秒）
     * 计算当前时间到令牌过期时间的间隔
     *
     * @param token JWT令牌字符串
     * @return long 剩余有效时间（毫秒），负数表示已过期
     * @throws JwtException 当令牌无效时抛出
     */
    public long getRemainingExpiration(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(getSigningKey())
                .build();
        Jws<Claims> jws = parser.parseSignedClaims(token);
        Date expiration = jws.getPayload().getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }
}