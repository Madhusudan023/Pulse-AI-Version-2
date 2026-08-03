package com.pulseai.reportingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "survey-service", path = "/api/v1/internal/surveys")
public interface SurveyFeignClient {

    @GetMapping("/{id}")
    Map<String, Object> getSurveyContext(@PathVariable("id") Long id);
}
