package com.ai.qa.service.application.service;

import com.ai.qa.service.api.dto.QAHistoryDTO; // 使用API层的DTO，确保与控制器层数据类型一致
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.domain.exception.QADomainException;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.domain.service.QAService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * QA历史记录应用服务
 * 负责协调领域服务和前端请求，处理应用层的业务逻辑
 * 提供问答历史的增删改查和统计功能
 *
 * 应用服务职责：
 * 1. 协调多个领域服务完成复杂业务逻辑
 * 2. 处理事务边界
 * 3. 数据转换（领域对象 -> DTO）
 * 4. 参数验证和业务规则执行
 */
@Service
@Transactional(readOnly = true) // 默认只读事务，写操作需要单独标注@Transactional
@RequiredArgsConstructor // Lombok注解，自动生成包含final字段的构造函数
public class QAHistoryService {

    // 依赖注入：QA历史记录仓库，负责数据持久化操作
    private final QAHistoryRepo qaHistoryRepo;

    // 依赖注入：QA领域服务，处理核心业务逻辑
    private final QAService qaService;

    /**
     * 保存问答历史记录
     * 验证参数后创建领域对象并持久化
     *
     * @param command 保存命令DTO，包含用户ID、问题、回答等信息
     * @return 保存后的历史记录DTO
     * @throws QADomainException 当参数验证失败时抛出领域异常
     */
    @Transactional // 写操作需要开启事务
    public QAHistoryDTO saveHistory(SaveHistoryCommand command) {
        // 参数验证：确保必要字段不为空
        validateSaveCommand(command);

        // 使用工厂方法创建领域对象：保持领域模型的完整性
        QAHistory history = QAHistory.createNew(
                command.getUserId(),
                command.getQuestion(),
                command.getAnswer(),
                command.getSessionId());

        // 调用仓库保存数据：基础设施层负责具体持久化实现
        QAHistory savedHistory = qaHistoryRepo.save(history);

        // 转换为DTO返回给调用方：隔离领域模型和API层
        return toDto(savedHistory);
    }

    /**
     * 查询用户问答历史
     * 根据查询条件获取用户的问答记录列表
     *
     * @param query 查询参数DTO，包含用户ID、分页等信息
     * @return 用户问答历史DTO列表
     * @throws QADomainException 当用户ID为空时抛出领域异常
     */
    public List<QAHistoryDTO> queryUserHistory(QAHistoryQuery query) {
        // 验证用户ID：确保查询条件有效
        validateUserId(query.getUserId());

        // 调用仓库获取数据：基础设施层执行数据库查询
        List<QAHistory> historyList = qaHistoryRepo.findHistoryByUserId(query.getUserId());

        // 转换为DTO列表：应用层负责数据展示格式转换
        return toDtoList(historyList);
    }

    /**
     * 分页查询用户问答历史
     * 支持分页和排序的用户问答记录查询
     *
     * @param query 查询参数DTO
     * @return 分页的用户问答历史DTO
     * @throws QADomainException 当用户ID为空时抛出领域异常
     */
    public Page<QAHistoryDTO> queryUserHistoryPage(QAHistoryQuery query) {
        // 验证用户ID
        validateUserId(query.getUserId());

        // 创建分页请求：封装分页和排序参数
        PageRequest pageRequest = createPageRequest(query);

        // 获取分页数据：目前是内存分页，后续可优化为数据库分页
        List<QAHistory> historyList = qaHistoryRepo.findHistoryByUserId(query.getUserId());
        long totalCount = qaHistoryRepo.countByUserId(query.getUserId());

        // 转换为DTO列表并封装分页结果
        List<QAHistoryDTO> dtoList = toDtoList(historyList);
        return new PageImpl<>(dtoList, pageRequest, totalCount);
    }

    /**
     * 查询会话问答历史
     * 获取特定会话中的所有问答记录
     *
     * @param sessionId 会话ID
     * @return 会话问答历史DTO列表
     * @throws QADomainException 当会话ID为空时抛出领域异常
     */
    public List<QAHistoryDTO> querySessionHistory(String sessionId) {
        // 验证会话ID：确保查询条件有效
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new QADomainException("会话ID不能为空");
        }

        // 调用仓库获取会话历史数据
        List<QAHistory> historyList = qaHistoryRepo.findHistoryBySession(sessionId);

        // 转换为DTO列表返回
        return toDtoList(historyList);
    }

    /**
     * 根据ID获取问答记录
     * 通过记录ID精确查询单条问答历史
     *
     * @param id 记录ID
     * @return 问答记录DTO
     * @throws RuntimeException 如果记录不存在时抛出运行时异常
     */
    public QAHistoryDTO getHistoryById(String id) {
        // 调用领域服务获取记录：领域服务处理业务逻辑和异常
        QAHistory history = qaService.getHistoryById(id);

        // 转换为DTO返回
        return toDto(history);
    }

    /**
     * 删除问答记录
     * 根据记录ID删除指定的问答历史
     *
     * @param id 要删除的记录ID
     */
    @Transactional // 写操作需要事务
    public void deleteHistory(String id) {
        // 委托给领域服务执行删除操作
        qaService.deleteHistory(id);
    }

    /**
     * 获取用户问答统计
     * 统计指定用户的问答记录数量
     *
     * @param userId 用户ID
     * @return 问答记录数量
     * @throws QADomainException 当用户ID为空时抛出领域异常
     */
    public long getUserHistoryCount(String userId) {
        // 验证用户ID
        validateUserId(userId);

        // 调用领域服务获取统计数量
        return qaService.getUserHistoryCount(userId);
    }

    /**
     * 清空会话历史
     * 删除指定会话中的所有问答记录
     *
     * @param sessionId 会话ID
     * @throws QADomainException 当会话ID为空时抛出领域异常
     */
    @Transactional // 写操作需要事务
    public void clearSessionHistory(String sessionId) {
        // 验证会话ID
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new QADomainException("会话ID不能为空");
        }

        // 委托给领域服务执行清空操作
        qaService.clearSessionHistory(sessionId);
    }

    /**
     * 验证保存命令参数
     * 确保保存操作的必要参数不为空
     *
     * @param command 保存命令DTO
     * @throws QADomainException 当参数验证失败时抛出领域异常
     */
    private void validateSaveCommand(SaveHistoryCommand command) {
        if (command.getUserId() == null || command.getUserId().trim().isEmpty()) {
            throw new QADomainException("用户ID不能为空");
        }
        if (command.getQuestion() == null || command.getQuestion().trim().isEmpty()) {
            throw new QADomainException("问题不能为空");
        }
        if (command.getAnswer() == null || command.getAnswer().trim().isEmpty()) {
            throw new QADomainException("回答不能为空");
        }
    }

    /**
     * 验证用户ID
     * 确保用户ID参数有效
     *
     * @param userId 用户ID
     * @throws QADomainException 当用户ID为空时抛出领域异常
     */
    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new QADomainException("用户ID不能为空");
        }
    }

    /**
     * 创建分页请求
     * 根据查询参数构建Spring Data的分页请求对象
     *
     * @param query 查询参数DTO
     * @return 分页请求对象
     */
    private PageRequest createPageRequest(QAHistoryQuery query) {
        return PageRequest.of(
                Math.max(query.getPage() - 1, 0), // 确保页码不小于0（前端页码从1开始，后端从0开始）
                Math.max(query.getSize(), 1), // 确保每页大小不小于1
                query.getDesc() ? Sort.by(Sort.Direction.DESC, "timestamp") : Sort.by(Sort.Direction.ASC, "timestamp"));
    }

    /**
     * 将领域对象转换为DTO
     * 领域模型 -> API数据传输对象
     *
     * @param history QA历史记录领域对象
     * @return QA历史记录DTO对象
     */
    private QAHistoryDTO toDto(QAHistory history) {
        QAHistoryDTO dto = new QAHistoryDTO();
        dto.setId(history.getId());
        dto.setUserId(history.getUserId());
        dto.setQuestion(history.getQuestion());
        dto.setAnswer(history.getAnswer());
        dto.setTimestamp(history.getTimestamp());
        dto.setSessionId(history.getSessionId());
        dto.setCreateTime(history.getCreateTime());
        dto.setUpdateTime(history.getUpdateTime());
        dto.setShortAnswer(history.getShortAnswer(100)); // 截取前100字符作为简略回答
        dto.setDuration(history.getDuration()); // 计算问答持续时间
        return dto;
    }

    /**
     * 将领域对象列表转换为DTO列表
     * 批量转换领域对象为API层可用的数据传输对象
     *
     * @param historyList QA历史记录领域对象列表
     * @return QA历史记录DTO对象列表
     */
    private List<QAHistoryDTO> toDtoList(List<QAHistory> historyList) {
        return historyList.stream()
                .map(this::toDto) // 使用Stream API进行批量转换
                .collect(Collectors.toList());
    }
}