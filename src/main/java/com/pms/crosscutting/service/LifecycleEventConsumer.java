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
    
    private final ConcurrentLinkedQueue<LifecycleEventEntity> eventQueue = new ConcurrentLinkedQueue<>();
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

    @KafkaListener(topicPartitions = @TopicPartition(topic = "lifecycle.event", partitions = "0"))
    public void consume(String payload) {
        log.debug("Kafka message received, queue size: {}", eventQueue.size());
        
        try {
            LifecycleEventDto eventDto = jsonUtil.fromJson(payload, LifecycleEventDto.class);
            if (eventDto != null) {
                LifecycleEventEntity entity = mapper.toEntity(eventDto);
                eventQueue.offer(entity);
                log.debug("Added event to queue for traceId: {}", eventDto.getTraceId());
                
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
        
        List<LifecycleEventEntity> batch = new ArrayList<>();
        LifecycleEventEntity event;
        
        while (!eventQueue.isEmpty() && batch.size() < BATCH_SIZE) {
            event = eventQueue.poll();
            if (event != null) {
                batch.add(event);
            }
        }
        
        if (!batch.isEmpty()) {
            try {
                repository.saveAll(batch);
                log.info("Batch saved {} lifecycle events to database", batch.size());
            } catch (Exception e) {
                log.error("Error saving batch of {} events: {}", batch.size(), e.getMessage());
                // Re-queue failed events
                batch.forEach(eventQueue::offer);
            }
        }
    }
}