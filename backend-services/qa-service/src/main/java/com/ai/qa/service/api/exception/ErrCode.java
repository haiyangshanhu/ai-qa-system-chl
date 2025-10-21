package com.ai.qa.service.api.exception;

/**
 * 错误码枚举
 * 定义系统错误码和错误信息
 */
public final class ErrCode {

    /**
     * 成功
     */
    public static final String SUCCESS = "0000";
    public static final String SUCCESS_MSG = "成功";

    /**
     * 通用业务错误
     */
    public static final String BAD_REQUEST = "4000";
    public static final String BAD_REQUEST_MSG = "请求参数错误";

    public static final String UNAUTHORIZED = "4001";
    public static final String UNAUTHORIZED_MSG = "未授权访问";

    public static final String FORBIDDEN = "4003";
    public static final String FORBIDDEN_MSG = "禁止访问";

    public static final String NOT_FOUND = "4004";
    public static final String NOT_FOUND_MSG = "资源不存在";

    public static final String INTERNAL_ERROR = "5000";
    public static final String INTERNAL_ERROR_MSG = "系统内部错误";

    /**
     * QA服务特定错误
     */
    public static final String QA_HISTORY_NOT_FOUND = "4100";
    public static final String QA_HISTORY_NOT_FOUND_MSG = "问答记录不存在";

    public static final String QA_USER_ID_REQUIRED = "4101";
    public static final String QA_USER_ID_REQUIRED_MSG = "用户ID不能为空";

    public static final String QA_QUESTION_REQUIRED = "4102";
    public static final String QA_QUESTION_REQUIRED_MSG = "问题不能为空";

    public static final String QA_ANSWER_REQUIRED = "4103";
    public static final String QA_ANSWER_REQUIRED_MSG = "回答不能为空";

    public static final String QA_SESSION_ID_REQUIRED = "4104";
    public static final String QA_SESSION_ID_REQUIRED_MSG = "会话ID不能为空";

    public static final String QA_PROCESS_FAILED = "4105";
    public static final String QA_PROCESS_FAILED_MSG = "问题处理失败";

    /**
     * 用户服务错误
     */
    public static final String USER_SERVICE_UNAVAILABLE = "4200";
    public static final String USER_SERVICE_UNAVAILABLE_MSG = "用户服务不可用";

    public static final String USER_NOT_FOUND = "4201";
    public static final String USER_NOT_FOUND_MSG = "用户不存在";

    /**
     * 数据库错误
     */
    public static final String DB_OPERATION_FAILED = "4300";
    public static final String DB_OPERATION_FAILED_MSG = "数据库操作失败";

    /**
     * 根据错误码获取错误信息
     *
     * @param errCode 错误码
     * @return 错误信息
     */
    public static String getErrMsg(String errCode) {
        switch (errCode) {
            case SUCCESS:
                return SUCCESS_MSG;
            case BAD_REQUEST:
                return BAD_REQUEST_MSG;
            case UNAUTHORIZED:
                return UNAUTHORIZED_MSG;
            case FORBIDDEN:
                return FORBIDDEN_MSG;
            case NOT_FOUND:
                return NOT_FOUND_MSG;
            case INTERNAL_ERROR:
                return INTERNAL_ERROR_MSG;
            case QA_HISTORY_NOT_FOUND:
                return QA_HISTORY_NOT_FOUND_MSG;
            case QA_USER_ID_REQUIRED:
                return QA_USER_ID_REQUIRED_MSG;
            case QA_QUESTION_REQUIRED:
                return QA_QUESTION_REQUIRED_MSG;
            case QA_ANSWER_REQUIRED:
                return QA_ANSWER_REQUIRED_MSG;
            case QA_SESSION_ID_REQUIRED:
                return QA_SESSION_ID_REQUIRED_MSG;
            case QA_PROCESS_FAILED:
                return QA_PROCESS_FAILED_MSG;
            case USER_SERVICE_UNAVAILABLE:
                return USER_SERVICE_UNAVAILABLE_MSG;
            case USER_NOT_FOUND:
                return USER_NOT_FOUND_MSG;
            case DB_OPERATION_FAILED:
                return DB_OPERATION_FAILED_MSG;
            default:
                return "未知错误";
        }
    }
}