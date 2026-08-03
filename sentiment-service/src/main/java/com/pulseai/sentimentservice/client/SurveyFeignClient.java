package com.pulseai.sentimentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "survey-service", path = "/api/v1/internal/surveys")
public interface SurveyFeignClient {

    @GetMapping("/{id}")
    Map<String, Object> getSurveyContext(@PathVariable("id") Long id);

    @GetMapping("/{id}/questions")
    List<Map<String, Object>> getSurveyQuestions(@PathVariable("id") Long id);

    @GetMapping("/{id}/responses")
    List<Map<String, Object>> getSurveyResponses(@PathVariable("id") Long id);

    @org.springframework.web.bind.annotation.PutMapping("/{id}/status")
    void updateSurveyStatus(@PathVariable("id") Long id, @org.springframework.web.bind.annotation.RequestParam("status") String status);
}
