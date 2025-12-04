package com.pms.crosscutting.service;

import com.pms.crosscutting.dto.LifecycleEventDto;
import com.pms.crosscutting.entity.LifecycleEventEntity;
import com.pms.crosscutting.mapper.LifecycleEventMapper;
import com.pms.crosscutting.repository.LifecycleEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LifecycleQueryService {

    private final LifecycleEventRepository repository;
    private final LifecycleEventMapper mapper;

    public List<LifecycleEventDto> findByTraceId(String traceId) {
        List<LifecycleEventEntity> entities = repository.findByTraceIdOrderByTimestampAsc(traceId);
        return entities.stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<LifecycleEventDto> findByPortfolioId(String portfolioId) {
        List<LifecycleEventEntity> entities = repository.findByPortfolioIdOrderByTimestampDesc(portfolioId);
        return entities.stream()
                .map(mapper::toDto)
                .toList();
    }
}