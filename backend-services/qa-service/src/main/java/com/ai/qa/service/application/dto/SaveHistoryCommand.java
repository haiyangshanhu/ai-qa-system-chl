package com.ai.qa.service.application.dto;

import lombok.Data;

/**
 * 保存历史记录命令DTO
 * 用于接收前端保存问答历史的请求参数
 */
@Data
public class SaveHistoryCommand {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户问题
     */
    private String question;

    /**
     * AI回答
     */
    private String answer;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * RAG上下文（可选）
     */
    private String ragContext;
}