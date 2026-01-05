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
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PreDestroy;

@Service
@RequiredArgsConstructor
@Slf4j
public class LifecycleEventConsumer {
    
    private final LifecycleEventRepository repository;
    private final LifecycleEventMapper mapper;
    private final JsonUtil jsonUtil;
    
    private static class EventWrapper {
        LifecycleEventEntity entity;
        int partition;
        EventWrapper(LifecycleEventEntity entity, int partition) {
            this.entity = entity;
            this.partition = partition;
        }
    }
    
    private final ConcurrentLinkedQueue<EventWrapper> eventQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int BATCH_SIZE = 200;
    private static final int BATCH_TIMEOUT_SECONDS = 5;
    
    @PostConstruct
    public void init() {
        log.info("LifecycleEventConsumer initialized with batch processing (size: {}, timeout: {}s)", BATCH_SIZE, BATCH_TIMEOUT_SECONDS);
        scheduler.scheduleAtFixedRate(this::processBatch, BATCH_TIMEOUT_SECONDS, BATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    
    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        processBatch();
    }

    @KafkaListener(
        topicPartitions = {
            @TopicPartition(topic = "lifecycle.event", partitions = {"1", "2", "3"})
        },
        groupId = "pms-crossref-group"
    )
    public void consume(String payload, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.debug("Kafka message received from partition {}, queue size: {}", partition, eventQueue.size());
        
        try {
            LifecycleEventDto eventDto = jsonUtil.fromJson(payload, LifecycleEventDto.class);
            if (eventDto != null) {
                LifecycleEventEntity entity = mapper.toEntity(eventDto);
                eventQueue.offer(new EventWrapper(entity, partition));
                log.debug("Added event to queue for traceId: {} from partition {}", eventDto.getTraceId(), partition);
                
                if (eventQueue.size() >= BATCH_SIZE) {
                    processBatch();
                }
            }
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage());
        }
    }
    
    @Transactional
    private void processBatch() {
        if (eventQueue.isEmpty()) {
            return;
        }
        
        List<EventWrapper> batch = new ArrayList<>();
        EventWrapper wrapper;
        
        while (!eventQueue.isEmpty() && batch.size() < BATCH_SIZE) {
            wrapper = eventQueue.poll();
            if (wrapper != null) {
                batch.add(wrapper);
            }
        }
        
        if (!batch.isEmpty()) {
            try {
                for (EventWrapper ew : batch) {
                    if (ew.partition == 1) {
                        repository.save(ew.entity);
                    } else if (ew.partition == 2 || ew.partition == 3) {
                        List<LifecycleEventEntity> existing = repository.findByTraceIdOrderByTimestampAsc(ew.entity.getTraceId());
                        if (!existing.isEmpty()) {
                            LifecycleEventEntity existingEntity = existing.get(0);
                            existingEntity.setStage(ew.entity.getStage());
                            existingEntity.setStatus(ew.entity.getStatus());
                            existingEntity.setTimestamp(ew.entity.getTimestamp());
                            existingEntity.setDetails(ew.entity.getDetails());
                            repository.save(existingEntity);
                        }
                    }
                }
                log.info("Batch processed {} lifecycle events", batch.size());
            } catch (Exception e) {
                log.error("Error processing batch of {} events: {}", batch.size(), e.getMessage());
                batch.forEach(eventQueue::offer);
            }
        }
    }
}