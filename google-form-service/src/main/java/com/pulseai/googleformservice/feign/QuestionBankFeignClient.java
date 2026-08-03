package com.pulseai.googleformservice.feign;

import com.pulseai.googleformservice.dto.QuestionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "question-bank-service", path = "/api/v1/internal/questions")
public interface QuestionBankFeignClient {

    @GetMapping("/{id}")
    QuestionResponseDTO getQuestionById(@PathVariable("id") Long id);
}
