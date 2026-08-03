package com.pulseai.surveyservice.controller;

import com.pulseai.surveyservice.entity.Answer;
import com.pulseai.surveyservice.entity.Survey;
import com.pulseai.surveyservice.entity.SurveyQuestion;
import com.pulseai.surveyservice.entity.SurveyResponse;
import com.pulseai.surveyservice.repository.AnswerRepository;
import com.pulseai.surveyservice.repository.SurveyQuestionRepository;
import com.pulseai.surveyservice.repository.SurveyRepository;
import com.pulseai.surveyservice.repository.SurveyResponseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@RestController
@RequestMapping("/api/v1/internal/surveys")
@Tag(name = "InternalSurvey APIs")
public class InternalSurveyController {

    private final SurveyRepository surveyRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final AnswerRepository answerRepository;

    @Operation(summary = "Endpoint for InternalSurvey")
    @GetMapping("/{id}")
    public ResponseEntity<Survey> getSurvey(@PathVariable Long id) {
        return ResponseEntity.ok(surveyRepository.findById(id).orElse(null));
    }

    @Operation(summary = "Endpoint for InternalSurvey")
    @GetMapping("/{id}/questions")
    public ResponseEntity<List<SurveyQuestion>> getSurveyQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(surveyQuestionRepository.findBySurveyIdOrderByDisplayOrderAsc(id));
    }

    @Operation(summary = "Endpoint for InternalSurvey")
    @GetMapping("/{id}/responses")
    public ResponseEntity<List<com.pulseai.surveyservice.dto.response.FullSurveyResponseDTO>> getSurveyResponses(@PathVariable Long id) {
        List<SurveyResponse> responses = surveyResponseRepository.findBySurveyId(id);
        List<Long> responseIds = responses.stream().map(SurveyResponse::getId).collect(Collectors.toList());
        List<Answer> allAnswers = answerRepository.findByResponseIdIn(responseIds);
        
        List<com.pulseai.surveyservice.dto.response.FullSurveyResponseDTO> result = responses.stream().map(res -> {
            com.pulseai.surveyservice.dto.response.FullSurveyResponseDTO dto = new com.pulseai.surveyservice.dto.response.FullSurveyResponseDTO();
            dto.setResponse(res);
            dto.setAnswers(allAnswers.stream().filter(a -> a.getResponseId().equals(res.getId())).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Endpoint for InternalSurvey")
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateSurveyStatus(@PathVariable Long id, @RequestParam String status) {
        Survey survey = surveyRepository.findById(id).orElse(null);
        if (survey != null) {
            survey.setStatus(com.pulseai.surveyservice.enums.SurveyStatus.valueOf(status));
            if ("ARCHIVED".equals(status)) {
                survey.setAiProcessed(true);
            }
            surveyRepository.save(survey);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    public InternalSurveyController(SurveyRepository surveyRepository, SurveyQuestionRepository surveyQuestionRepository, SurveyResponseRepository surveyResponseRepository, AnswerRepository answerRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.surveyResponseRepository = surveyResponseRepository;
        this.answerRepository = answerRepository;
    }
}
