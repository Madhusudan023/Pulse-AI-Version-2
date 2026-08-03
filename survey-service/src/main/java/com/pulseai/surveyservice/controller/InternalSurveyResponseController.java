package com.pulseai.surveyservice.controller;

import com.pulseai.surveyservice.dto.ApiResponse;
import com.pulseai.surveyservice.dto.request.SubmitSurveyRequest;
import com.pulseai.surveyservice.service.InternalSurveyResponseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/surveys")
public class InternalSurveyResponseController {

    private final InternalSurveyResponseService internalSurveyResponseService;

    public InternalSurveyResponseController(InternalSurveyResponseService internalSurveyResponseService) {
        this.internalSurveyResponseService = internalSurveyResponseService;
    }

    @PostMapping("/{id}/responses")
    public ResponseEntity<ApiResponse<Void>> submitInternalResponse(
            @PathVariable Long id, 
            @RequestBody SubmitSurveyRequest submitRequest) {
        // null employeeId denotes anonymous/external submission
        internalSurveyResponseService.submitSurveyInternal(id, null, submitRequest);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Internal survey response saved successfully").build());
    }
}
