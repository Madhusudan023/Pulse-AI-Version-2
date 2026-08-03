package com.pulseai.surveyservice.client;

import com.pulseai.surveyservice.dto.event.SurveyClosedEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sentiment-service", path = "/api/internal/sentiment")
public interface SentimentFeignClient {

    @PostMapping("/trigger-analysis")
    void triggerAnalysis(@RequestBody SurveyClosedEvent event);
}
