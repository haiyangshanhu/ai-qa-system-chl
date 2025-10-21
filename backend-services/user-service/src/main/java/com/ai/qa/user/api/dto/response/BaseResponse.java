package com.ai.qa.user.api.dto.response;

import com.ai.qa.user.api.exception.ErrCode;

import lombok.Data;

/**
 * 基础响应数据传输对象
 * 所有API响应的基类，提供统一的响应格式和结构
 * 包含操作结果、消息和错误代码等通用字段
 *
 */
@Data
public class BaseResponse {

    /**
     * 操作是否成功
     * 标识本次请求的处理结果状态
     * true: 操作成功，false: 操作失败
     *
     * @apiNote 示例值: true
     */
    private Boolean success;

    /**
     * 响应消息
     * 对操作结果的文字描述，可用于前端显示
     * 成功时显示成功信息，失败时显示错误原因
     *
     * @apiNote 示例值: "操作成功" 或 "用户不存在"
     */
    private String message;

    /**
     * 错误代码
     * 数字化的操作结果标识，便于程序处理
     * 成功时通常为200，失败时为具体的错误代码
     *
     * @apiNote 示例值: 200 或 1001
     * @see ErrCode
     */
    private Integer errorCode;

    /**
     * 创建成功响应对象的静态工厂方法
     * 使用预定义的成功消息和错误代码
     *
     * @return BaseResponse 包含成功信息的响应对象
     * @apiNote 通常用于操作成功的场景
     */
    public static BaseResponse success() {
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage(ErrCode.MSG_SUCCESS);
        response.setErrorCode(ErrCode.SUCCESS);
        return response;
    }

    /**
     * 创建错误响应对象的静态工厂方法
     * 支持自定义错误消息和错误代码
     *
     * @param message 错误消息描述
     * @param errorCode 错误代码
     * @return BaseResponse 包含错误信息的响应对象
     * @apiNote 通常用于操作失败的场景
     */
    public static BaseResponse error(String message, Integer errorCode) {
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrorCode(errorCode);
        return response;
    }

    /**
     * 创建错误响应对象的便捷方法（使用预定义错误常量）
     *
     * @param errCode 错误代码
     * @param errMessage 错误消息
     * @return BaseResponse 包含错误信息的响应对象
     */
    public static BaseResponse error(Integer errCode, String errMessage) {
        return error(errMessage, errCode);
    }

    /**
     * 快速创建业务异常对应的错误响应
     *
     * @param errCode 错误代码
     * @return BaseResponse 包含错误信息的响应对象
     */
    public static BaseResponse error(Integer errCode) {
        String message = "";
        switch (errCode) {
            case ErrCode.USER_NOT_FOUND:
                message = ErrCode.MSG_USER_NOT_FOUND;
                break;
            case ErrCode.USER_ALREADY_EXISTS:
                message = ErrCode.MSG_USER_ALREADY_EXISTS;
                break;
            case ErrCode.PASSWORD_INCORRECT:
                message = ErrCode.MSG_PASSWORD_INCORRECT;
                break;
            case ErrCode.PASSWORD_MISMATCH:
                message = ErrCode.MSG_PASSWORD_MISMATCH;
                break;
            default:
                message = ErrCode.MSG_BAD_REQUEST;
        }
        return error(message, errCode);
    }
}