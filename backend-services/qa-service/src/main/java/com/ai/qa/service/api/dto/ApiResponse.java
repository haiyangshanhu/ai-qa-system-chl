package com.ai.qa.service.api.dto;

import com.ai.qa.service.api.exception.ErrCode;
import lombok.Data;

/**
 * 统一API响应格式
 * 用于规范所有接口的返回数据格式，包含状态码、消息和数据
 * @param <T> 响应数据的类型
 */
@Data
public class ApiResponse<T> {

    /**
     * 响应状态码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 构造函数
     * @param code 状态码
     * @param message 消息
     * @param data 数据
     */
    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功的ApiResponse实例
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrCode.SUCCESS, ErrCode.SUCCESS_MSG, data);
    }

    /**
     * 创建错误响应
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误的ApiResponse实例
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 创建错误响应（根据错误码自动获取错误消息）
     * @param code 错误码
     * @param <T> 数据类型
     * @return 错误的ApiResponse实例
     */
    public static <T> ApiResponse<T> error(String code) {
        return new ApiResponse<>(code, ErrCode.getErrMsg(code), null);
    }
}