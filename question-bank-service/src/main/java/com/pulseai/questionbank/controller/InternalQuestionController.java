package com.pulseai.questionbank.controller;

import com.pulseai.questionbank.dto.response.QuestionResponseDTO;
import com.pulseai.questionbank.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@RestController
@RequestMapping("/api/v1/internal/questions")
@Tag(name = "InternalQuestion APIs")
public class InternalQuestionController {

    private final QuestionService questionService;

    @Operation(summary = "Endpoint for InternalQuestion")
    @GetMapping("/approved")
    public ResponseEntity<List<QuestionResponseDTO>> getApprovedQuestions(
            @RequestParam String region,
            @RequestParam String type) {
        return ResponseEntity.ok(questionService.getApprovedQuestionsByRegionAndType(region, type));
    }

    @Operation(summary = "Endpoint for InternalQuestion")
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @Operation(summary = "Endpoint for InternalQuestion")
    @PostMapping("/ai")
    public ResponseEntity<Void> addAiDraftQuestions(@RequestBody List<com.pulseai.questionbank.dto.request.CreateQuestionRequest> requests) {
        for (var req : requests) {
            questionService.createAiDraftQuestion(req);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Endpoint for InternalQuestion")
    @PostMapping("/bulk-fetch")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByIds(@RequestBody List<Long> ids) {
        List<QuestionResponseDTO> questions = ids.stream()
                .map(questionService::getQuestionById)
                .toList();
        return ResponseEntity.ok(questions);
    }
    public InternalQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
}
