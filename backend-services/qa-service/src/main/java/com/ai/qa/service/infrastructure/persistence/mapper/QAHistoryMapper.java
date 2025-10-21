package com.ai.qa.service.infrastructure.persistence.mapper;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * QA历史记录映射器
 * 用于领域对象QAHistory和持久化对象QAHistoryPO之间的转换
 * 使用MapStruct实现自动映射，避免手动编写转换代码
 */
@Mapper(componentModel = "spring")
public interface QAHistoryMapper {

    /**
     * MapStruct实例，用于非Spring环境下的手动调用
     */
    QAHistoryMapper INSTANCE = Mappers.getMapper(QAHistoryMapper.class);

    /**
     * 将领域对象转换为持久化对象
     *
     * @param domain 领域对象
     * @return 持久化对象
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "question", target = "question")
    @Mapping(source = "answer", target = "answer")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "sessionId", target = "sessionId")
    @Mapping(source = "createTime", target = "createTime")
    @Mapping(source = "updateTime", target = "updateTime")
    QAHistoryPO toPO(QAHistory domain);

    /**
     * 将持久化对象转换为领域对象
     *
     * @param po 持久化对象
     * @return 领域对象
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "question", target = "question")
    @Mapping(source = "answer", target = "answer")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "sessionId", target = "sessionId")
    @Mapping(source = "createTime", target = "createTime")
    @Mapping(source = "updateTime", target = "updateTime")
    QAHistory toDomain(QAHistoryPO po);
}