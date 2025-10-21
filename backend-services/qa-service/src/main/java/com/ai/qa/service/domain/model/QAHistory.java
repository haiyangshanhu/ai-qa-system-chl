package com.ai.qa.service.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * QA历史记录领域模型
 * 包含用户问答的核心业务逻辑和状态管理
 */
@Getter
@Setter
public class QAHistory {

    /**
     * 唯一标识符
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户提出的问题
     */
    private String question;

    /**
     * AI生成的回答
     */
    private String answer;

    /**
     * 问答发生的时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 会话ID，用于关联同一会话中的多条记录
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
     * 私有构造函数，确保通过工厂方法创建对象
     * 保持领域模型的完整性和一致性
     */
    public QAHistory() {
        this.timestamp = LocalDateTime.now();
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 创建新的QA历史记录（工厂方法）
     *
     * @param userId    用户ID
     * @param question  用户问题
     * @param answer    AI回答
     * @param sessionId 会话ID
     * @param rag       RAG上下文
     * @return 新创建的QAHistory实例
     */
    public static QAHistory createNew(String userId, String question, String answer,
            String sessionId) {
        QAHistory history = new QAHistory();
        history.userId = userId;
        history.question = question;
        history.answer = answer;
        history.sessionId = sessionId;
        return history;
    }

    /**
     * 更新回答内容
     *
     * @param newAnswer 新的回答内容
     */
    public void updateAnswer(String newAnswer) {
        this.answer = newAnswer;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 验证QA历史记录的有效性
     *
     * @return true如果记录有效，否则false
     */
    public boolean isValid() {
        return userId != null && !userId.trim().isEmpty() &&
                question != null && !question.trim().isEmpty() &&
                answer != null && !answer.trim().isEmpty();
    }

    /**
     * 获取简化的回答（用于显示）
     *
     * @param maxLength 最大显示长度
     * @return 简化后的回答内容
     */
    public String getShortAnswer(int maxLength) {
        if (answer == null)
            return "";
        return answer.length() > maxLength ? answer.substring(0, maxLength) + "..." : answer;
    }

    /**
     * 获取问答持续时间（如果适用）
     *
     * @return 问答处理时长描述
     */
    public String getDuration() {
        if (createTime == null || updateTime == null)
            return "unknown";
        long seconds = java.time.Duration.between(createTime, updateTime).getSeconds();
        return seconds + "s";
    }
}