package com.pulseai.reportingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "sentiment-service", path = "/api/v1/internal/reports")
public interface SentimentFeignClient {

    @GetMapping("/{surveyId}")
    Map<String, Object> getReportBySurveyId(@PathVariable("surveyId") Long surveyId);
}
