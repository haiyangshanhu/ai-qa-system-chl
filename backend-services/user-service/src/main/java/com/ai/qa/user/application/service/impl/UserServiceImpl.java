package com.ai.qa.user.application.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.request.UpdatePasswordRequest;
import com.ai.qa.user.api.dto.response.LoginResponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import com.ai.qa.user.api.dto.response.UpdatePasswordResponse;
import com.ai.qa.user.api.dto.response.UserResponse;
import com.ai.qa.user.api.exception.BusinessException;
import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.common.JwtUtil;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;

/**
 * 用户服务实现类
 * 处理用户相关的业务逻辑，包括登录、注册等功能
 *
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录处理
     * 验证用户名和密码，生成访问令牌
     *
     * @param loginRequest 登录请求参数，包含用户名和密码
     * @return LoginResponse 登录响应结果，包含令牌和用户信息
     * @throws BusinessException 当用户不存在或密码错误时抛出业务异常
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        log.info("Start login(), username:{}", loginRequest.getUsername());

        // 根据用户名查询用户信息
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        // 验证用户是否存在
        if (!userOptional.isPresent()) {
            throw BusinessException.userNotFound();
        }

        User user = userOptional.get();

        // 验证密码是否正确
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw BusinessException.passwordIncorrect();
        }

        // 更新用户最后登录时间
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        // 生成访问令牌（模拟实现，实际应使用JWT）
        String token = generateToken(user);

        // 构建登录成功响应
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setMessage(ErrCode.MSG_SUCCESS);
        response.setErrorCode(ErrCode.SUCCESS);
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setEmail(user.getEmail());
        response.setLoginTime(LocalDateTime.now());

        log.info("End login(), username:{}", loginRequest.getUsername());

        return response;
    }

    /**
     * 用户注册处理
     * 创建新用户账户，验证用户名唯一性和密码一致性
     *
     * @param registerRequest 注册请求参数，包含用户名、昵称、密码等信息
     * @return RegisterResponse 注册响应结果，包含新创建的用户信息
     * @throws BusinessException 当用户名已存在或密码不匹配时抛出业务异常
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {

        log.info("Start register(), username:{}", registerRequest.getUsername());

        // 检查用户名是否已被注册
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw BusinessException.userAlreadyExists();
        }

        // 验证两次输入的密码是否一致
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw BusinessException.passwordMismatch();
        }

        // 创建新用户对象并设置属性
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setNickname(registerRequest.getNickname());
        user.setEmail(registerRequest.getEmail());
        // 对密码进行加密存储
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreateDate(LocalDateTime.now());
        user.setUpdateDate(LocalDateTime.now());

        // 保存用户到数据库
        User savedUser = userRepository.save(user);

        // 构建注册成功响应
        RegisterResponse response = new RegisterResponse();
        response.setSuccess(true);
        response.setMessage(ErrCode.MSG_CREATED);
        response.setErrorCode(ErrCode.CREATED);
        response.setUserId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setNickname(savedUser.getNickname());
        response.setEmail(user.getEmail());
        response.setRegisterTime(LocalDateTime.now());

        log.info("End register(), username:{}", registerRequest.getUsername());

        return response;
    }

    /**
     * 修改用户密码处理
     * 验证旧密码正确性，更新为新密码
     *
     * @param updatePasswordRequest 修改密码请求参数
     * @return UpdatePasswordResponse 修改密码响应结果
     * @throws BusinessException 当用户不存在、旧密码错误或新密码不匹配时抛出业务异常
     */
    @Override
    @Transactional
    public UpdatePasswordResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {

        log.info("Start updatePassword(), username:{}", updatePasswordRequest.getUsername());

        // 根据用户名查询用户信息
        Optional<User> userOptional = userRepository.findByUsername(updatePasswordRequest.getUsername());

        // 验证用户是否存在
        if (!userOptional.isPresent()) {
            throw BusinessException.userNotFound();
        }

        User user = userOptional.get();

        // 验证旧密码是否正确
        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
            throw BusinessException.passwordIncorrect();
        }

        // 验证两次输入的新密码是否一致
        if (!updatePasswordRequest.getNewPassword().equals(updatePasswordRequest.getConfirmNewPassword())) {
            throw BusinessException.passwordMismatch();
        }

        // 更新密码（加密存储）
        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        user.setUpdateDate(LocalDateTime.now());

        // 保存更新后的用户信息
        User updatedUser = userRepository.save(user);

        // 构建密码修改成功响应
        UpdatePasswordResponse response = new UpdatePasswordResponse();
        response.setSuccess(true);
        response.setMessage("密码修改成功");
        response.setErrorCode(ErrCode.SUCCESS);
        response.setUserId(updatedUser.getId());
        response.setUsername(updatedUser.getUsername());
        response.setUpdateTime(LocalDateTime.now());

        log.info("End updatePassword(), username:{}", updatePasswordRequest.getUsername());

        return response;
    }

    /**
     * 根据用户ID查询用户信息
     * 用于获取指定用户ID的完整用户信息
     *
     * @param id 用户ID
     * @return UserResponse 用户响应对象，包含用户信息和操作结果
     * @throws BusinessException 当用户不存在时抛出业务异常
     */
    @Override
    public UserResponse getUserById(Long id) {
        log.info("Querying user by ID: {}", id);

        Optional<User> userOptional = userRepository.getUserById(id);

        if (!userOptional.isPresent()) {
            log.warn("User not found, id:{}", id);
            throw BusinessException.userNotFound();
        }

        User user = userOptional.get();
        return convertToUserResponse(user);
    }

    /**
     * 根据用户名查询用户信息
     * 用于获取指定用户名的完整用户信息
     *
     * @param username 用户名
     * @return UserResponse 用户响应对象，包含用户信息和操作结果
     * @throws BusinessException 当用户不存在时抛出业务异常
     */
    @Override
    public UserResponse findByUsername(String username) {
        log.info("Querying user by username: {}", username);

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            log.warn("User not found, username:{}", username);
            throw BusinessException.userNotFound();
        }

        User user = userOptional.get();
        return convertToUserResponse(user);
    }

    /**
     * 检查用户名是否已存在
     *
     * @param username 需要检查的用户名
     * @return Boolean true表示用户名已存在，false表示不存在
     */
    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 将User实体转换为UserResponse DTO
     *
     * @param user User实体对象
     * @return UserResponse 响应DTO对象
     */
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setSuccess(true);
        response.setMessage(ErrCode.MSG_SUCCESS);
        response.setErrorCode(ErrCode.SUCCESS);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRegisterTime(user.getCreateDate());
        return response;
    }

    /**
     * 生成用户访问令牌（模拟实现）
     * 实际项目中应使用JWT等标准token生成机制
     *
     * @param user 用户实体对象
     * @return String 生成的访问令牌
     */
    private String generateToken(User user) {
        // 调用JwtUtil生成真实token
        return jwtUtil.generateToken(user.getUsername());
    }

    // /**
    // * 验证用户密码是否正确
    // *
    // * @param rawPassword 原始密码（用户输入）
    // * @param encodedPassword 加密后的密码（数据库存储）
    // * @return Boolean true表示密码正确，false表示密码错误
    // */
    // private Boolean validatePassword(String rawPassword, String encodedPassword)
    // {
    // return passwordEncoder.matches(rawPassword, encodedPassword);
    // }

    // /**
    // * 加密用户密码
    // *
    // * @param rawPassword 原始密码
    // * @return String 加密后的密码
    // */
    // private String encodePassword(String rawPassword) {
    // return passwordEncoder.encode(rawPassword);
    // }

}