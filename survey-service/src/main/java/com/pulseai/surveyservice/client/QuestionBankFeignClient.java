package com.pulseai.surveyservice.client;

import com.pulseai.surveyservice.dto.response.QuestionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "question-bank-service", path = "/api/v1/internal/questions")
public interface QuestionBankFeignClient {

    @GetMapping("/approved")
    List<QuestionResponseDTO> getApprovedQuestions(@RequestParam("region") String region, @RequestParam("type") String type);

    @GetMapping("/{id}")
    QuestionResponseDTO getQuestionById(@PathVariable("id") Long id);
}
