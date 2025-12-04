package com.pms.crosscutting.controller;

import com.pms.crosscutting.dto.LifecycleEventDto;
import com.pms.crosscutting.service.LifecycleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lifecycle")
@RequiredArgsConstructor
public class LifecycleQueryController {

    private final LifecycleQueryService queryService;

    @GetMapping("/{traceId}")
    public ResponseEntity<List<LifecycleEventDto>> getByTraceId(@PathVariable String traceId) {
        List<LifecycleEventDto> events = queryService.findByTraceId(traceId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<List<LifecycleEventDto>> getByPortfolioId(@PathVariable String portfolioId) {
        List<LifecycleEventDto> events = queryService.findByPortfolioId(portfolioId);
        return ResponseEntity.ok(events);
    }
}