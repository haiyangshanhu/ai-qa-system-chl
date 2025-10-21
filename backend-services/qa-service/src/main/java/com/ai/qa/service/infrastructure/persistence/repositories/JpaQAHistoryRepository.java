package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * QA历史记录JPA数据访问接口
 * 继承JpaRepository获得基础的CRUD操作能力
 * 使用@Query注解定义自定义的JPQL查询语句
 *
 * 注意：此接口位于基础设施层，负责与数据库的直接交互
 */
@Repository
public interface JpaQAHistoryRepository extends JpaRepository<QAHistoryPO, Long> {

    /**
     * 根据主键ID查找QA历史记录
     * 此方法由JPA自动实现，无需编写SQL
     *
     * @param id 主键ID
     * @return 包含QA历史记录的Optional对象
     */
    Optional<QAHistoryPO> findById(Long id);

    /**
     * 根据用户ID查找所有QA历史记录，按时间降序排列
     * 使用JPQL自定义查询语句
     *
     * @param userId 用户ID
     * @return 该用户的所有QA历史记录列表，按时间倒序排列
     */
    @Query("SELECT q FROM QAHistoryPO q WHERE q.userId = :userId ORDER BY q.timestamp DESC")
    List<QAHistoryPO> findByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID分页查找QA历史记录
     * 支持分页和排序，提高大数据量查询性能
     *
     * @param userId   用户ID
     * @param pageable 分页参数，包含页码、每页大小和排序信息
     * @return 分页后的QA历史记录列表
     */
    @Query("SELECT q FROM QAHistoryPO q WHERE q.userId = :userId")
    List<QAHistoryPO> findByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * 根据会话ID查找所有QA历史记录，按时间降序排列
     * 用于获取同一会话中的完整对话历史
     *
     * @param sessionId 会话ID
     * @return 该会话的所有QA历史记录列表，按时间倒序排列
     */
    @Query("SELECT q FROM QAHistoryPO q WHERE q.sessionId = :sessionId ORDER BY q.timestamp DESC")
    List<QAHistoryPO> findBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据用户ID和会话ID联合查找QA历史记录，按时间降序排列
     * 用于精确获取特定用户在特定会话中的问答记录
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 符合条件的QA历史记录列表，按时间倒序排列
     */
    @Query("SELECT q FROM QAHistoryPO q WHERE q.userId = :userId AND q.sessionId = :sessionId ORDER BY q.timestamp DESC")
    List<QAHistoryPO> findByUserIdAndSessionId(@Param("userId") String userId, @Param("sessionId") String sessionId);

    /**
     * 统计指定用户的问答记录数量
     * 用于用户行为分析和统计报表
     *
     * @param userId 用户ID
     * @return 该用户的问答记录总数
     */
    @Query("SELECT COUNT(q) FROM QAHistoryPO q WHERE q.userId = :userId")
    long countByUserId(@Param("userId") String userId);

    /**
     * 根据会话ID统计问答记录数量
     * 用于会话长度监控和清理策略
     *
     * @param sessionId 会话ID
     * @return 该会话的问答记录总数
     */
    @Query("SELECT COUNT(q) FROM QAHistoryPO q WHERE q.sessionId = :sessionId")
    long countBySessionId(@Param("sessionId") String sessionId);

    /**
     * 删除指定用户的全部问答记录
     * 用于用户数据清理或账号注销功能
     *
     * @param userId 用户ID
     * @return 删除的记录数量
     */
    @Query("DELETE FROM QAHistoryPO q WHERE q.userId = :userId")
    long deleteByUserId(@Param("userId") String userId);

    /**
     * 删除指定会话的全部问答记录
     * 用于会话数据清理和维护
     *
     * @param sessionId 会话ID
     * @return 删除的记录数量
     */
    @Query("DELETE FROM QAHistoryPO q WHERE q.sessionId = :sessionId")
    long deleteBySessionId(@Param("sessionId") String sessionId);
}