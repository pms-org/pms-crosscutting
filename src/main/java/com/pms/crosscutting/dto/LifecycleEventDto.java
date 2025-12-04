package com.pms.crosscutting.dto;

import lombok.Data;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
public class LifecycleEventDto {
    private UUID traceId;
    private UUID portfolioId;
    private String stage;
    private String status;
    private Instant ts;  // matches "ts" field from ingestion
    private Map<String, Object> details;  // matches details object
}