package com.pulseai.questionbank.controller;

import com.pulseai.questionbank.dto.ApiResponse;
import com.pulseai.questionbank.dto.request.CreateQuestionRequest;
import com.pulseai.questionbank.dto.request.UpdateQuestionRequest;
import com.pulseai.questionbank.dto.response.QuestionResponseDTO;
import com.pulseai.questionbank.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@RestController
@RequestMapping("/api/v1/questions")
@Tag(name = "Question APIs")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "Endpoint for Question")
    @PostMapping
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> createQuestion(@RequestBody CreateQuestionRequest request, jakarta.servlet.http.HttpServletRequest httpRequest) {
        String region = (String) httpRequest.getAttribute("region");
        if (request.getRegion() == null) {
            request.setRegion(region);
        }
        QuestionResponseDTO response = questionService.createQuestion(request);
        return ResponseEntity.ok(ApiResponse.<QuestionResponseDTO>builder().success(true).data(response).build());
    }

    @Operation(summary = "Endpoint for Question")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> updateQuestion(@PathVariable Long id, @RequestBody UpdateQuestionRequest request) {
        QuestionResponseDTO response = questionService.updateQuestion(id, request);
        return ResponseEntity.ok(ApiResponse.<QuestionResponseDTO>builder().success(true).data(response).build());
    }

    @Operation(summary = "Endpoint for Question")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Deleted successfully").build());
    }

    @Operation(summary = "Endpoint for Question")
    @GetMapping
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<List<QuestionResponseDTO>>> getAllQuestions(
            @RequestParam(required = false) com.pulseai.questionbank.enums.QuestionStatus status,
            jakarta.servlet.http.HttpServletRequest request) {
        String region = (String) request.getAttribute("region");
        return ResponseEntity.ok(ApiResponse.<List<QuestionResponseDTO>>builder().success(true).data(questionService.getAllQuestions(status, region)).build());
    }

    @Operation(summary = "Endpoint for Question")
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<Void>> approveQuestion(@PathVariable Long id) {
        questionService.approveQuestion(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Question approved").build());
    }

    @Operation(summary = "Endpoint for Question")
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<Void>> rejectQuestion(@PathVariable Long id) {
        questionService.rejectQuestion(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Question rejected").build());
    }
    @Operation(summary = "Get Onboarding Questions")
    @GetMapping("/onboarding")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<List<QuestionResponseDTO>>> getOnboardingQuestions(jakarta.servlet.http.HttpServletRequest request) {
        String region = (String) request.getAttribute("region");
        return ResponseEntity.ok(ApiResponse.<List<QuestionResponseDTO>>builder().success(true).data(questionService.getApprovedQuestionsByRegionAndType(region, "ONBOARDING")).build());
    }

    @Operation(summary = "Get Monthly Pulse Questions")
    @GetMapping("/monthly-pulse")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<List<QuestionResponseDTO>>> getMonthlyPulseQuestions(jakarta.servlet.http.HttpServletRequest request) {
        String region = (String) request.getAttribute("region");
        return ResponseEntity.ok(ApiResponse.<List<QuestionResponseDTO>>builder().success(true).data(questionService.getApprovedQuestionsByRegionAndType(region, "MONTHLY_PULSE")).build());
    }

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
}
