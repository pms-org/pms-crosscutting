package com.pms.crosscutting.service;

import com.pms.crosscutting.dto.LifecycleEventDto;
import com.pms.crosscutting.entity.LifecycleEventEntity;
import com.pms.crosscutting.mapper.LifecycleEventMapper;
import com.pms.crosscutting.repository.LifecycleEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

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
        UUID uuid = UUID.fromString(portfolioId);
        List<LifecycleEventEntity> entities = repository.findByPortfolioIdOrderByTimestampDesc(uuid);
        return entities.stream()
                .map(mapper::toDto)
                .toList();
    }
}