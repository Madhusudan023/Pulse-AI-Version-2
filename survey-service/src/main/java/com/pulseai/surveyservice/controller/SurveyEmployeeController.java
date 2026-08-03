package com.pulseai.surveyservice.controller;

import com.pulseai.surveyservice.dto.ApiResponse;
import com.pulseai.surveyservice.dto.request.SubmitSurveyRequest;
import com.pulseai.surveyservice.service.SurveyEmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/employee/surveys")
@Tag(name = "SurveyEmployee APIs")
public class SurveyEmployeeController {

    private final SurveyEmployeeService surveyEmployeeService;

    public SurveyEmployeeController(SurveyEmployeeService surveyEmployeeService) {
        this.surveyEmployeeService = surveyEmployeeService;
    }

    @Operation(summary = "Get Survey Details and Questions for Employee")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getSurveyDetails(
            @PathVariable Long id,
            jakarta.servlet.http.HttpServletRequest request) {
        Object empIdObj = request.getAttribute("employeeId");
        Long employeeId = empIdObj instanceof Number ? ((Number) empIdObj).longValue() : null;
        java.util.Map<String, Object> details = surveyEmployeeService.getSurveyDetails(id, employeeId);
        return ResponseEntity.ok(ApiResponse.<java.util.Map<String, Object>>builder()
                .success(true).message("Survey details retrieved").data(details).build());
    }

    @Operation(summary = "Endpoint for SurveyEmployee")
    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<Void>> submitSurvey(
            @PathVariable Long id, 
            jakarta.servlet.http.HttpServletRequest request,
            @RequestBody SubmitSurveyRequest submitRequest) {
        Object empIdObj = request.getAttribute("employeeId");
        Long employeeId = empIdObj instanceof Number ? ((Number) empIdObj).longValue() : null;
        String employeeEmail = (String) request.getAttribute("employeeEmail");
        surveyEmployeeService.submitSurvey(id, employeeId, employeeEmail, submitRequest);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Survey submitted successfully").build());
    }

    @Operation(summary = "Get Employee Surveys by Status")
    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<com.pulseai.surveyservice.entity.Survey>>> getSurveys(
            @RequestParam com.pulseai.surveyservice.enums.AssignmentStatus status,
            jakarta.servlet.http.HttpServletRequest request) {
        Object empIdObj = request.getAttribute("employeeId");
        Long employeeId = empIdObj instanceof Number ? ((Number) empIdObj).longValue() : null;
        java.util.List<com.pulseai.surveyservice.entity.Survey> surveys = surveyEmployeeService.getSurveysByStatus(employeeId, status);
        return ResponseEntity.ok(ApiResponse.<java.util.List<com.pulseai.surveyservice.entity.Survey>>builder()
                .success(true).message("Surveys retrieved").data(surveys).build());
    }
}
