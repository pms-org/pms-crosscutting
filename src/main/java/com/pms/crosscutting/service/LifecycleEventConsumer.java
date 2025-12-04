package com.pms.crosscutting.service;

import com.pms.crosscutting.dto.LifecycleEventDto;
import com.pms.crosscutting.entity.LifecycleEventEntity;
import com.pms.crosscutting.mapper.LifecycleEventMapper;
import com.pms.crosscutting.repository.LifecycleEventRepository;
import com.pms.crosscutting.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Slf4j
public class LifecycleEventConsumer {
    
    @PostConstruct
    public void init() {
        log.info("LifecycleEventConsumer initialized and ready to consume messages");
    }

    private final LifecycleEventRepository repository;
    private final LifecycleEventMapper mapper;
    private final JsonUtil jsonUtil;

    @KafkaListener(topicPartitions = @TopicPartition(topic = "lifecycle.event", partitions = "0"))
    public void consume(String payload) {
        log.info("=== KAFKA MESSAGE RECEIVED ===");
        log.info("Raw Kafka message received: {}", payload);
        log.info("Message length: {}", payload != null ? payload.length() : "null");
        
        try {
            // Parse JSON string to DTO
            LifecycleEventDto eventDto = jsonUtil.fromJson(payload, LifecycleEventDto.class);
            if (eventDto != null) {
                LifecycleEventEntity entity = mapper.toEntity(eventDto);
                repository.save(entity);
                log.info("Saved lifecycle event for traceId: {}", eventDto.getTraceId());
            }
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage());
        }
    }
}