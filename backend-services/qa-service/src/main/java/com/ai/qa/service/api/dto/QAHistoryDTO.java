package com.ai.qa.service.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * QA历史记录响应DTO
 * 用于返回给前端的问答历史数据
 */
@Data
public class QAHistoryDTO {

    /**
     * 记录ID
     */
    private Long id;

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
     * 问答时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;

    /**
     * 记录最后更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 简化的回答（用于列表显示）
     */
    private String shortAnswer;

    /**
     * 问答持续时间
     */
    private String duration;
}