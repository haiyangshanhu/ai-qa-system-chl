package com.ai.qa.user.api.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于处理系统中的业务逻辑异常，包含错误代码和错误信息
 *
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误代码
     * 用于标识具体的错误类型，便于前后端统一处理
     */
    private final Integer errorCode;

    /**
     * 错误消息
     * 对错误的详细描述，可用于前端显示给用户
     */
    private final String errorMessage;

    /**
     * 构造业务异常
     *
     * @param errorCode    错误代码，参考ErrCode中定义的常量
     * @param errorMessage 错误消息描述
     */
    public BusinessException(Integer errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 构造业务异常（带原始异常）
     *
     * @param errorCode    错误代码，参考ErrCode中定义的常量
     * @param errorMessage 错误消息描述
     * @param cause        原始异常，用于异常链追踪
     */
    public BusinessException(Integer errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // ========== 常用业务异常快捷方法 ========== //

    /**
     * 用户不存在异常
     *
     * @return BusinessException 用户不存在业务异常
     */
    public static BusinessException userNotFound() {
        return new BusinessException(ErrCode.USER_NOT_FOUND, ErrCode.MSG_USER_NOT_FOUND);
    }

    /**
     * 用户已存在异常
     * 通常在用户注册时，用户名重复时抛出
     *
     * @return BusinessException 用户已存在业务异常
     */
    public static BusinessException userAlreadyExists() {
        return new BusinessException(ErrCode.USER_ALREADY_EXISTS, ErrCode.MSG_USER_ALREADY_EXISTS);
    }

    /**
     * 密码不正确异常
     * 用户登录时密码验证失败时抛出
     *
     * @return BusinessException 密码不正确业务异常
     */
    public static BusinessException passwordIncorrect() {
        return new BusinessException(ErrCode.PASSWORD_INCORRECT, ErrCode.MSG_PASSWORD_INCORRECT);
    }

    /**
     * 密码不匹配异常
     * 用户注册或修改密码时，两次输入的密码不一致时抛出
     *
     * @return BusinessException 密码不匹配业务异常
     */
    public static BusinessException passwordMismatch() {
        return new BusinessException(ErrCode.PASSWORD_MISMATCH, ErrCode.MSG_PASSWORD_MISMATCH);
    }

    /**
     * 无效token异常
     * JWT token验证失败时抛出
     *
     * @return BusinessException 无效token业务异常
     */
    public static BusinessException invalidToken() {
        return new BusinessException(ErrCode.INVALID_TOKEN, ErrCode.MSG_INVALID_TOKEN);
    }

    /**
     * 请求参数错误异常
     * 通用的参数验证失败异常
     *
     * @param message 具体的错误消息
     * @return BusinessException 请求参数错误业务异常
     */
    public static BusinessException badRequest(String message) {
        return new BusinessException(ErrCode.BAD_REQUEST, message);
    }

    /**
     * 自定义业务异常
     * 用于创建特定的业务异常
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     * @return BusinessException 自定义业务异常
     */
    public static BusinessException of(Integer errorCode, String errorMessage) {
        return new BusinessException(errorCode, errorMessage);
    }

    /**
     * 自定义业务异常（带原始异常）
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     * @param cause        原始异常
     * @return BusinessException 自定义业务异常
     */
    public static BusinessException of(Integer errorCode, String errorMessage, Throwable cause) {
        return new BusinessException(errorCode, errorMessage, cause);
    }

    /**
     * 重写toString方法，便于日志输出
     *
     * @return 异常信息的字符串表示
     */
    @Override
    public String toString() {
        return "BusinessException{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }

    /**
     * 获取详细的异常信息
     * 包含错误代码和错误消息
     *
     * @return 详细的异常信息字符串
     */
    public String getDetailMessage() {
        return "ErrorCode: " + errorCode + ", Message: " + errorMessage;
    }
}