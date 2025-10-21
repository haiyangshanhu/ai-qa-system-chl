package com.ai.qa.service.domain.exception;

/**
 * QA领域异常
 * 用于表示问答业务逻辑中的错误情况
 */
public class QADomainException extends RuntimeException {

    /**
     * 构造带有详细消息的领域异常
     *
     * @param message 异常详细信息
     */
    public QADomainException(String message) {
        super(message);
    }

    /**
     * 构造带有详细消息和原因的领域异常
     *
     * @param message 异常详细信息
     * @param cause 异常原因
     */
    public QADomainException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 获取异常类型
     *
     * @return 异常类型标识
     */
    public String getExceptionType() {
        return "QA_DOMAIN_EXCEPTION";
    }
}