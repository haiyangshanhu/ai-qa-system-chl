package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import com.ai.qa.service.infrastructure.persistence.mapper.QAHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * QA历史记录仓库实现类
 * 实现领域层的QAHistoryRepo接口，提供具体的持久化操作
 * 使用JpaQAHistoryRepository进行数据库操作，使用Mapper进行对象转换
 */
@Component
@RequiredArgsConstructor
public class QAHistoryRepoImpl implements QAHistoryRepo {

    private final JpaQAHistoryRepository jpaQAHistoryRepository;
    private final QAHistoryMapper mapper;

    /**
     * 保存QA历史记录
     *
     * @param history 领域对象
     * @return 保存后的领域对象（包含生成的ID）
     */
    @Override
    public QAHistory save(QAHistory history) {
        QAHistoryPO qaHistoryPO = mapper.toPO(history);
        QAHistoryPO savedPO = jpaQAHistoryRepository.save(qaHistoryPO);
        return mapper.toDomain(savedPO);
    }

    /**
     * 根据ID查找QA历史记录
     *
     * @param id 记录ID（字符串格式）
     * @return 包含QA历史记录的Optional对象
     */
    @Override
    public Optional<QAHistory> findHistoryById(String id) {
        try {
            Long historyId = Long.parseLong(id);
            Optional<QAHistoryPO> qaHistoryPO = jpaQAHistoryRepository.findById(historyId);
            return qaHistoryPO.map(mapper::toDomain);
        } catch (NumberFormatException e) {
            // 记录ID格式错误，返回空Optional
            return Optional.empty();
        }
    }

    /**
     * 根据会话ID查找QA历史记录
     *
     * @param sessionId 会话ID
     * @return 该会话的所有QA历史记录列表
     */
    @Override
    public List<QAHistory> findHistoryBySession(String sessionId) {
        List<QAHistoryPO> historyPOs = jpaQAHistoryRepository.findBySessionId(sessionId);
        return historyPOs.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID查找QA历史记录
     *
     * @param userId 用户ID
     * @return 该用户的所有QA历史记录列表
     */
    @Override
    public List<QAHistory> findHistoryByUserId(String userId) {
        List<QAHistoryPO> historyPOs = jpaQAHistoryRepository.findByUserId(userId);
        return historyPOs.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID和会话ID查找QA历史记录
     *
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 符合条件的QA历史记录列表
     */
    @Override
    public List<QAHistory> findHistoryByUserIdAndSessionId(String userId, String sessionId) {
        List<QAHistoryPO> historyPOs = jpaQAHistoryRepository.findByUserIdAndSessionId(userId, sessionId);
        return historyPOs.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * 删除QA历史记录
     *
     * @param id 要删除的记录ID
     */
    @Override
    public void deleteById(String id) {
        try {
            Long historyId = Long.parseLong(id);
            jpaQAHistoryRepository.deleteById(historyId);
        } catch (NumberFormatException e) {
            // 记录日志：ID格式错误，忽略删除操作
            // log.warn("Invalid ID format for deletion: {}", id);
        }
    }

    /**
     * 获取用户最近的QA历史记录
     *
     * @param userId 用户ID
     * @param limit 返回记录数量限制
     * @return 最近的QA历史记录列表
     */
    @Override
    public List<QAHistory> findRecentHistoryByUserId(String userId, int limit) {
        // 创建分页请求，按时间戳降序排列
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));

        // 调用JPA仓库的分页查询方法
        List<QAHistoryPO> historyPOs = jpaQAHistoryRepository.findByUserId(userId, pageRequest);

        return historyPOs.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * 统计用户的问答数量
     *
     * @param userId 用户ID
     * @return 该用户的问答记录总数
     */
    @Override
    public long countByUserId(String userId) {
        return jpaQAHistoryRepository.countByUserId(userId);
    }
}