package com.pms.crosscutting.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lifecycle_events")
@Data
public class LifecycleEventEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trace_id", nullable = false)
    private String traceId;
    
    @Column(name = "portfolio_id")
    private UUID portfolioId;
    
    @Column(name = "stage")
    private String stage;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
}