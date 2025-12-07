package com.pms.crosscutting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
public class LifecycleEventDto {
    private String traceId;
    private UUID portfolioId;
    private String stage;
    private String status;
    @JsonProperty("ts")
    private Instant timestamp;
    private Map<String, Object> details;
}