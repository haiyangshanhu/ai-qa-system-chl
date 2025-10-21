package com.ai.qa.user.api.exception;

/**
 * 异常定数定义
 *
 */
public final class ErrCode {

    // 成功状态码
    public static final int SUCCESS = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;

    // 客户端错误状态码
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;

    // 服务端错误状态码
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int SERVICE_UNAVAILABLE = 503;

    // 业务错误码（可在基础HTTP状态码上扩展）
    public static final int USER_NOT_FOUND = 1001;
    public static final int USER_ALREADY_EXISTS = 1002;
    public static final int PASSWORD_INCORRECT = 1003;
    public static final int PASSWORD_MISMATCH = 1004;
    public static final int INVALID_TOKEN = 1005;
    public static final int PASSWORD_UPDATE_FAILED = 1006;

    // 错误消息
    public static final String MSG_SUCCESS = "操作成功";
    public static final String MSG_CREATED = "创建成功";
    public static final String MSG_USER_NOT_FOUND = "用户不存在";
    public static final String MSG_USER_ALREADY_EXISTS = "用户已存在";
    public static final String MSG_PASSWORD_INCORRECT = "密码错误";
    public static final String MSG_PASSWORD_MISMATCH = "密码不匹配";
    public static final String MSG_INVALID_TOKEN = "无效的token";
    public static final String MSG_BAD_REQUEST = "请求参数错误";
    public static final String MSG_INTERNAL_ERROR = "服务器内部错误";
    public static final String MSG_PASSWORD_UPDATE_FAILED = "密码修改失败";

    private ErrCode() {
        // 防止实例化
    }
}