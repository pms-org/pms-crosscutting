package com.pms.crosscutting.repository;

import com.pms.crosscutting.entity.LifecycleEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface LifecycleEventRepository extends JpaRepository<LifecycleEventEntity, Long> {
    
    List<LifecycleEventEntity> findByTraceIdOrderByTimestampAsc(String traceId);
    
    List<LifecycleEventEntity> findByPortfolioIdOrderByTimestampDesc(UUID portfolioId);
}