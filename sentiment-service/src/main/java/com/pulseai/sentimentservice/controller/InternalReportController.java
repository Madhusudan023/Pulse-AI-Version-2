package com.pulseai.sentimentservice.controller;

import com.pulseai.sentimentservice.entity.SentimentReport;
import com.pulseai.sentimentservice.repository.SentimentReportRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@RestController
@RequestMapping("/api/v1/internal/reports")
@Tag(name = "InternalReport APIs")
public class InternalReportController {

    private final SentimentReportRepository sentimentReportRepository;

    @Operation(summary = "Endpoint for InternalReport")
    @GetMapping("/{surveyId}")
    public ResponseEntity<SentimentReport> getReportBySurveyId(@PathVariable Long surveyId) {
        return sentimentReportRepository.findFirstBySurveyIdOrderByGeneratedAtDesc(surveyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    public InternalReportController(SentimentReportRepository sentimentReportRepository) {
        this.sentimentReportRepository = sentimentReportRepository;
    }
}
