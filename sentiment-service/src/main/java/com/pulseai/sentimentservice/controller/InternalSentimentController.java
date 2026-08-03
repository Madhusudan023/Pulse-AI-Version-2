package com.pulseai.sentimentservice.controller;

import com.pulseai.sentimentservice.dto.event.SurveyClosedEvent;
import com.pulseai.sentimentservice.service.SentimentAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/sentiment")
public class InternalSentimentController {

    private final SentimentAnalysisService sentimentAnalysisService;

    public InternalSentimentController(SentimentAnalysisService sentimentAnalysisService) {
        this.sentimentAnalysisService = sentimentAnalysisService;
    }

    @PostMapping("/trigger-analysis")
    public ResponseEntity<Void> triggerAnalysis(@RequestBody SurveyClosedEvent event) {
        sentimentAnalysisService.processSurveyClosedEvent(event);
        return ResponseEntity.ok().build();
    }
}
