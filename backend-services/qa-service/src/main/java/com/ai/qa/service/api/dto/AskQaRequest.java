package com.ai.qa.service.api.dto;

import lombok.Data;

/**
 * QA问答请求DTO
 * 用于接收前端发送的问答请求参数
 */
@Data
public class AskQaRequest {
    private Long userId;
    private String question;
    private String sessionId;
}
