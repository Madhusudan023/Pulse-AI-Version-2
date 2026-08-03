package com.pulseai.sentimentservice.client;

import com.pulseai.sentimentservice.dto.request.CreateQuestionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "question-bank-service", path = "/api/v1/internal/questions")
public interface QuestionBankFeignClient {

    @PostMapping("/ai")
    void addAiDraftQuestions(@RequestBody List<CreateQuestionRequest> requests);

    @PostMapping("/bulk-fetch")
    List<java.util.Map<String, Object>> getQuestionsByIds(@RequestBody List<Long> ids);
}
