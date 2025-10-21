package com.ai.qa.service.domain.repo;

import com.ai.qa.service.domain.model.QAHistory;
import java.util.List;
import java.util.Optional;

/**
 * QA历史记录仓库接口
 * 定义领域层与基础设施层的契约，提供数据访问抽象
 */
public interface QAHistoryRepo {

    /**
     * 保存QA历史记录
     *
     * @param history 要保存的QA历史记录
     * @return 保存后的QA历史记录（包含生成的ID）
     */
    QAHistory save(QAHistory history);

    /**
     * 根据ID查找QA历史记录
     *
     * @param id 记录ID
     * @return 包含QA历史记录的Optional对象
     */
    Optional<QAHistory> findHistoryById(String id);

    /**
     * 根据会话ID查找QA历史记录
     *
     * @param sessionId 会话ID
     * @return 该会话的所有QA历史记录列表
     */
    List<QAHistory> findHistoryBySession(String sessionId);

    /**
     * 根据用户ID查找QA历史记录
     *
     * @param userId 用户ID
     * @return 该用户的所有QA历史记录列表
     */
    List<QAHistory> findHistoryByUserId(String userId);

    /**
     * 根据用户ID和会话ID查找QA历史记录
     *
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 符合条件的QA历史记录列表
     */
    List<QAHistory> findHistoryByUserIdAndSessionId(String userId, String sessionId);

    /**
     * 删除QA历史记录
     *
     * @param id 要删除的记录ID
     */
    void deleteById(String id);

    /**
     * 获取用户最近的QA历史记录
     *
     * @param userId 用户ID
     * @param limit 返回记录数量限制
     * @return 最近的QA历史记录列表
     */
    List<QAHistory> findRecentHistoryByUserId(String userId, int limit);

    /**
     * 统计用户的问答数量
     *
     * @param userId 用户ID
     * @return 该用户的问答记录总数
     */
    long countByUserId(String userId);
}