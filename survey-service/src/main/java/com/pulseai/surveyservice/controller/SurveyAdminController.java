package com.pulseai.surveyservice.controller;

import com.pulseai.surveyservice.dto.ApiResponse;
import com.pulseai.surveyservice.dto.request.AddQuestionsBulkRequest;
import com.pulseai.surveyservice.dto.request.CreateSurveyRequest;
import com.pulseai.surveyservice.entity.Survey;
import com.pulseai.surveyservice.service.SurveyAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@RestController
@RequestMapping("/api/v1/surveys")
@Tag(name = "SurveyAdmin APIs")
public class SurveyAdminController {

    private final SurveyAdminService surveyAdminService;

    @Operation(summary = "Endpoint for SurveyAdmin")
    @PostMapping
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<Survey>> createSurvey(@RequestBody CreateSurveyRequest request) {
        Survey survey = surveyAdminService.createSurvey(request);
        return ResponseEntity.ok(ApiResponse.<Survey>builder().success(true).data(survey).build());
    }

    @Operation(summary = "Endpoint for SurveyAdmin")
    @GetMapping("/my-region")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<java.util.List<Survey>>> getMyRegionSurveys(
            @RequestParam(required = false) com.pulseai.surveyservice.enums.SurveyStatus status,
            jakarta.servlet.http.HttpServletRequest request) {
        String region = (String) request.getAttribute("region");
        java.util.List<Survey> surveys = surveyAdminService.getSurveysByRegion(region, status);
        return ResponseEntity.ok(ApiResponse.<java.util.List<Survey>>builder().success(true).data(surveys).build());
    }

    @Operation(summary = "Endpoint for SurveyAdmin")
    @PostMapping("/{id}/questions/bulk")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<Void>> addQuestions(@PathVariable Long id, @RequestBody AddQuestionsBulkRequest request) {
        surveyAdminService.addQuestionsBulk(id, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Questions added").build());
    }

    @Operation(summary = "Endpoint for SurveyAdmin")
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<Void>> publishSurvey(@PathVariable Long id, @RequestBody(required = false) com.pulseai.surveyservice.dto.request.PublishSurveyRequest publishRequest) {
        surveyAdminService.publishSurvey(id, publishRequest != null ? publishRequest.getCustomEmails() : null);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Survey published and assignments created").build());
    }

    @Operation(summary = "Endpoint for SurveyAdmin")
    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<Void>> closeSurvey(@PathVariable Long id) {
        surveyAdminService.closeSurvey(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Survey closed manually").build());
    }

    @Operation(summary = "Endpoint for SurveyAdmin")
    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<Void>> reactivateSurvey(@PathVariable Long id) {
        surveyAdminService.reactivateSurvey(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Survey reactivated").build());
    }

    @Operation(summary = "Endpoint for SurveyAdmin")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<Survey>> updateSurvey(@PathVariable Long id, @RequestBody CreateSurveyRequest request) {
        Survey survey = surveyAdminService.updateSurvey(id, request);
        return ResponseEntity.ok(ApiResponse.<Survey>builder().success(true).data(survey).build());
    }

    @Operation(summary = "Endpoint for SurveyAdmin")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<Void>> deleteSurvey(@PathVariable Long id) {
        surveyAdminService.deleteSurvey(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Survey deleted successfully").build());
    }

    @Operation(summary = "Remove specific questions from a draft survey")
    @DeleteMapping("/{id}/questions/bulk")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<Void>> removeQuestions(@PathVariable Long id, @RequestBody java.util.List<Long> questionIds) {
        surveyAdminService.removeQuestionsFromSurvey(id, questionIds);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Questions removed from survey").build());
    }

    @Operation(summary = "Get question IDs from the last survey of a specific type")
    @GetMapping("/last/{surveyType}/question-ids")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<java.util.List<Long>>> getLastSurveyQuestionIds(@PathVariable String surveyType) {
        java.util.List<Long> questionIds = surveyAdminService.getLastSurveyQuestionIds(com.pulseai.surveyservice.enums.SurveyType.valueOf(surveyType));
        return ResponseEntity.ok(ApiResponse.<java.util.List<Long>>builder().success(true).data(questionIds).build());
    }

    @Operation(summary = "Get all non-anonymous responses for a survey")
    @GetMapping("/{id}/responses")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<java.util.List<com.pulseai.surveyservice.dto.response.FullSurveyResponseDTO>>> getSurveyResponses(@PathVariable Long id) {
        java.util.List<com.pulseai.surveyservice.dto.response.FullSurveyResponseDTO> responses = surveyAdminService.getNonAnonymousResponses(id);
        return ResponseEntity.ok(ApiResponse.<java.util.List<com.pulseai.surveyservice.dto.response.FullSurveyResponseDTO>>builder().success(true).data(responses).build());
    }

    @Operation(summary = "Get all questions for a survey")
    @GetMapping("/{id}/questions")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR')")
    public ResponseEntity<ApiResponse<java.util.List<com.pulseai.surveyservice.entity.SurveyQuestion>>> getSurveyQuestions(@PathVariable Long id) {
        java.util.List<com.pulseai.surveyservice.entity.SurveyQuestion> questions = surveyAdminService.getSurveyQuestions(id);
        return ResponseEntity.ok(ApiResponse.<java.util.List<com.pulseai.surveyservice.entity.SurveyQuestion>>builder().success(true).data(questions).build());
    }

    public SurveyAdminController(SurveyAdminService surveyAdminService) {
        this.surveyAdminService = surveyAdminService;
    }
}
