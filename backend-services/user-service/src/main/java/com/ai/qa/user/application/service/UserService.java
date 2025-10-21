package com.ai.qa.user.application.service;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.request.UpdatePasswordRequest;
import com.ai.qa.user.api.dto.response.LoginResponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import com.ai.qa.user.api.dto.response.UpdatePasswordResponse;
import com.ai.qa.user.api.dto.response.UserResponse;

/**
 * 用户服务接口
 * 定义用户相关的业务逻辑方法，包括身份认证、用户管理等功能
 * 作为业务逻辑层接口，负责处理核心业务规则和数据操作
 *
 */
public interface UserService {

    /**
     * 用户登录认证
     * 验证用户名和密码，生成访问令牌并返回用户信息
     *
     * @param loginRequest 登录请求参数，包含用户名和密码
     * @return LoginResponse 登录响应结果，包含令牌和用户信息
     * @throws com.ai.qa.user.api.exception.BusinessException 当用户不存在或密码错误时抛出业务异常
     * @see LoginRequest
     * @see LoginResponse
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 用户注册
     * 创建新用户账户，验证用户名唯一性和密码一致性
     *
     * @param registerRequest 注册请求参数，包含用户名、昵称、密码等信息
     * @return RegisterResponse 注册响应结果，包含新创建的用户信息
     * @throws com.ai.qa.user.api.exception.BusinessException 当用户名已存在或密码不匹配时抛出业务异常
     * @see RegisterRequest
     * @see RegisterResponse
     */
    RegisterResponse register(RegisterRequest registerRequest);

    /**
     * 根据用户ID查询用户信息
     * 用于获取指定用户ID的完整用户信息
     *
     * @param id 用户ID
     * @return UserResponse 用户响应对象，包含用户信息和操作结果
     * @throws com.ai.qa.user.api.exception.BusinessException 当用户不存在时抛出业务异常
     * @see UserResponse
     */
    UserResponse getUserById(Long id);

    /**
     * 根据用户名查询用户信息
     * 用于获取指定用户名的完整用户信息
     *
     * @param username 用户名
     * @return UserResponse 用户响应对象，包含用户信息和操作结果
     * @throws com.ai.qa.user.api.exception.BusinessException 当用户不存在时抛出业务异常
     * @see UserResponse
     */
    UserResponse findByUsername(String username);

    /**
     * 检查用户名是否存在
     * 用于验证用户名是否已被注册，通常在注册前调用
     *
     * @param username 需要检查的用户名
     * @return Boolean true表示用户名已存在，false表示用户名可用
     */
    Boolean existsByUsername(String username);

    /**
     * 修改用户密码
     * 验证旧密码后更新为新密码，需要用户已登录并通过JWT认证
     *
     * @param updatePasswordRequest 修改密码请求参数，包含用户ID、旧密码和新密码
     * @return UpdatePasswordResponse 修改密码响应结果
     */
    UpdatePasswordResponse updatePassword(UpdatePasswordRequest updatePasswordRequest);
}