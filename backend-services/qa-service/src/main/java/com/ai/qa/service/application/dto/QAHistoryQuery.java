package com.ai.qa.service.application.dto;

import lombok.Data;

/**
 * QA历史记录查询DTO
 * 用于接收前端查询参数
 */
@Data
public class QAHistoryQuery {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 是否按时间倒序排列
     */
    private Boolean desc = true;
}