package com.pulseai.googleformservice.feign;

import com.pulseai.googleformservice.dto.SurveyQuestionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.pulseai.googleformservice.dto.request.SubmitSurveyRequest;

import java.util.List;

@FeignClient(name = "survey-service", path = "/api/v1/internal/surveys")
public interface SurveyFeignClient {

    @GetMapping("/{id}/questions")
    List<SurveyQuestionDTO> getSurveyQuestions(@PathVariable("id") Long id);

    @PostMapping("/{id}/responses")
    void submitInternalResponse(@PathVariable("id") Long id, @RequestBody SubmitSurveyRequest submitRequest);
}
