package com.pms.crosscutting.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.crosscutting.dto.LifecycleEventDto;
import com.pms.crosscutting.entity.LifecycleEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LifecycleEventMapper {

    private final ObjectMapper objectMapper;

    public LifecycleEventEntity toEntity(LifecycleEventDto dto) {
        LifecycleEventEntity entity = new LifecycleEventEntity();
        entity.setTraceId(dto.getTraceId());
        entity.setPortfolioId(dto.getPortfolioId());
        entity.setStage(dto.getStage());
        entity.setStatus(dto.getStatus());
        entity.setTimestamp(LocalDateTime.ofInstant(dto.getTimestamp(), ZoneOffset.UTC));
        try {
            entity.setDetails(objectMapper.writeValueAsString(dto.getDetails()));
        } catch (Exception e) {
            entity.setDetails(dto.getDetails().toString());
        }
        return entity;
    }

    public LifecycleEventDto toDto(LifecycleEventEntity entity) {
        LifecycleEventDto dto = new LifecycleEventDto();
        dto.setTraceId(entity.getTraceId());
        dto.setPortfolioId(entity.getPortfolioId());
        dto.setStage(entity.getStage());
        dto.setStatus(entity.getStatus());
        dto.setTimestamp(entity.getTimestamp().toInstant(ZoneOffset.UTC));
        try {
            dto.setDetails(objectMapper.readValue(entity.getDetails(), Map.class));
        } catch (Exception e) {
            dto.setDetails(Map.of("raw", entity.getDetails()));
        }
        return dto;
    }
}