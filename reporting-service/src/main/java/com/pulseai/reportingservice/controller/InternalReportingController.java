package com.pulseai.reportingservice.controller;

import com.pulseai.reportingservice.dto.event.AIReportGeneratedEvent;
import com.pulseai.reportingservice.service.ReportingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/reporting")
public class InternalReportingController {

    private final ReportingService reportingService;

    public InternalReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @PostMapping("/save-report")
    public ResponseEntity<Void> saveReport(@RequestBody AIReportGeneratedEvent event) {
        reportingService.processAiReportGenerated(event);
        return ResponseEntity.ok().build();
    }
}
