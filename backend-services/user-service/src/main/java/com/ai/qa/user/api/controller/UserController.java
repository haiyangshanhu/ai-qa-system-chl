package com.ai.qa.user.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.request.UpdatePasswordRequest;
import com.ai.qa.user.api.dto.response.LoginResponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import com.ai.qa.user.api.dto.response.UpdatePasswordResponse;
import com.ai.qa.user.api.dto.response.UserResponse;
import com.ai.qa.user.application.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求，包括登录、注册等接口
 * 作为RESTful API的入口点，负责请求转发和响应处理
 *
 */
@Tag(name = "用户管理", description = "用户登录、注册等身份认证相关接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 用户登录接口
     * 接收登录请求，调用服务层进行身份验证，返回登录结果
     *
     * @param loginRequest 登录请求参数，包含用户名和密码
     * @return ResponseEntity<LoginResponse> 包含登录结果的响应实体
     * @apiNote POST /api/users/login
     * @example
     *          {
     *          "username": "testuser",
     *          "password": "password123"
     *          }
     */
    @Operation(summary = "用户登录", description = "通过用户名和密码进行身份验证")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户名或密码错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Parameter(description = "登录请求参数", required = true)
            @RequestBody LoginRequest loginRequest
    ) {
        log.info("Start login(), username:{}", loginRequest.getUsername());

        // 调用用户服务处理登录逻辑
        LoginResponse response = userService.login(loginRequest);

        log.info("End login(), username:{}", loginRequest.getUsername());

        // 根据登录成功与否返回相应的HTTP状态码
        // 成功返回200 OK，失败时业务异常已在服务层抛出，不会执行到此处
        return ResponseEntity.ok(response);
    }

    /**
     * 用户注册接口
     * 接收注册请求，调用服务层创建新用户，返回注册结果
     *
     * @param registerRequest 注册请求参数，包含用户名、昵称、密码等信息
     * @return ResponseEntity<RegisterResponse> 包含注册结果的响应实体
     * @apiNote POST /api/users/register
     * @example
     *          {
     *          "username": "newuser",
     *          "nickname": "新用户",
     *          "password": "password123",
     *          "confirmPassword": "password123"
     *          }
     */
    @Operation(summary = "用户注册", description = "创建新的用户账户")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "注册成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "用户名已存在或密码不匹配"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "用户已存在")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Parameter(description = "注册请求参数", required = true)
            @RequestBody RegisterRequest registerRequest
    ) {
        log.info("Start register(), username:{}", registerRequest.getUsername());

        // 调用用户服务处理注册逻辑
        RegisterResponse response = userService.register(registerRequest);

        log.info("Start register(), username:{}", registerRequest.getUsername());

        // 注册成功返回201 Created状态码
        // 失败时业务异常已在服务层抛出，不会执行到此处
        return ResponseEntity.status(201).body(response);
    }

    /**
     * 根据用户ID查询用户信息
     * 获取指定用户ID的完整用户信息
     *
     * @param id 用户ID
     * @return ResponseEntity<UserResponse> 包含用户信息的响应实体
     * @apiNote GET /api/user/{id}
     * @example GET /api/user/1
     */
    @Operation(summary = "根据ID查询用户", description = "通过用户ID获取用户详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.info("Start getUserById(), id:{}", id);

        // 调用用户服务查询用户信息
        UserResponse response = userService.getUserById(id);

        log.info("End getUserById(), id:{}, username:{}", id, response.getUsername());

        return ResponseEntity.ok(response);
    }

    /**
     * 根据用户名查询用户信息
     * 获取指定用户名的完整用户信息
     *
     * @param username 用户名
     * @return ResponseEntity<UserResponse> 包含用户信息的响应实体
     * @apiNote GET /api/user/username/{username}
     * @example GET /api/user/username/testuser
     */
    @Operation(summary = "根据用户名查询用户", description = "通过用户名获取用户详细信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查询成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(
            @Parameter(description = "用户名", required = true, example = "testuser")
            @PathVariable String username
    ) {
        log.info("Start getUserByUsername(), username:{}", username);

        // 调用用户服务查询用户信息
        UserResponse response = userService.findByUsername(username);

        log.info("End getUserByUsername(), username:{}, userId:{}", username, response.getUserId());

        return ResponseEntity.ok(response);
    }

    /**
     * 修改用户密码接口
     * 接收密码修改请求，调用服务层进行密码更新，返回操作结果
     *
     * @param updatePasswordRequest 修改密码请求参数
     * @return ResponseEntity<UpdatePasswordResponse> 包含修改结果的响应实体
     * @apiNote POST /api/users/updatePassword
     * @example
     *          {
     *          "userId": 12345,
     *          "oldPassword": "oldPassword123",
     *          "newPassword": "newPassword456",
     *          "confirmNewPassword": "newPassword456"
     *          }
     */
    @Operation(summary = "修改密码", description = "验证旧密码后更新用户密码")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "密码修改成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "旧密码错误或新密码不匹配"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PostMapping("/updatePassword")
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            @Parameter(description = "修改密码请求参数", required = true)
            @RequestBody UpdatePasswordRequest updatePasswordRequest
    ) {
        log.info("Start updatePassword(), username:{}", updatePasswordRequest.getUsername());

        // 调用用户服务处理密码修改逻辑
        UpdatePasswordResponse response = userService.updatePassword(updatePasswordRequest);

        log.info("End updatePassword(), username:{}", updatePasswordRequest.getUsername());

        // 密码修改成功返回200 OK状态码
        return ResponseEntity.ok(response);
    }
}