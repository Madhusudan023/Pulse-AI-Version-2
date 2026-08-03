package com.pulseai.surveyservice.service;

import com.pulseai.surveyservice.dto.request.AnswerRequest;
import com.pulseai.surveyservice.dto.request.SubmitSurveyRequest;
import com.pulseai.surveyservice.entity.Answer;
import com.pulseai.surveyservice.entity.Survey;
import com.pulseai.surveyservice.entity.SurveyAssignment;
import com.pulseai.surveyservice.entity.SurveyResponse;
import com.pulseai.surveyservice.enums.AssignmentStatus;
import com.pulseai.surveyservice.enums.SurveyStatus;
import com.pulseai.surveyservice.exception.BusinessException;
import com.pulseai.surveyservice.exception.ResourceNotFoundException;
import com.pulseai.surveyservice.repository.AnswerRepository;
import com.pulseai.surveyservice.repository.SurveyAssignmentRepository;
import com.pulseai.surveyservice.repository.SurveyRepository;
import com.pulseai.surveyservice.repository.SurveyResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SurveyEmployeeService {

    private final SurveyRepository surveyRepository;
    private final SurveyAssignmentRepository surveyAssignmentRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final AnswerRepository answerRepository;
    private final com.pulseai.surveyservice.repository.SurveyQuestionRepository surveyQuestionRepository;

    @Transactional
    public void submitSurvey(Long surveyId, Long employeeId, String employeeEmail, SubmitSurveyRequest request) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));

        if (survey.getStatus() != SurveyStatus.ACTIVE) {
            throw new BusinessException("Survey is not currently active.");
        }

        SurveyAssignment assignment = surveyAssignmentRepository.findBySurveyIdAndEmployeeId(surveyId, employeeId)
                .orElseThrow(() -> new BusinessException("You are not assigned to this survey."));

        if (assignment.getStatus() == AssignmentStatus.COMPLETED) {
            throw new BusinessException("You have already submitted this survey.");
        }

        SurveyResponse response = new SurveyResponse();
        response.setSurveyId(surveyId);
        
        if (!survey.isAnonymous()) {
            response.setEmployeeId(employeeId);
            response.setEmployeeEmail(employeeEmail);
        } else {
            response.setEmployeeId(null);
            response.setEmployeeEmail(null);
        }
        
        response.setResponseDuration(request.getResponseDuration());
        response.setSubmittedAt(LocalDateTime.now());
        SurveyResponse savedResponse = surveyResponseRepository.save(response);

        for (AnswerRequest ansReq : request.getAnswers()) {
            Answer answer = new Answer();
            answer.setResponseId(savedResponse.getId());
            answer.setQuestionId(ansReq.getQuestionId());
            answer.setRatingAnswer(ansReq.getRatingAnswer());
            answer.setTextAnswer(ansReq.getTextAnswer());
            answer.setOptionAnswer(ansReq.getOptionAnswer());
            answerRepository.save(answer);
        }

        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignment.setSubmittedAt(LocalDateTime.now());
        surveyAssignmentRepository.save(assignment);
        
        survey.setCompletedParticipants(survey.getCompletedParticipants() + 1);
        surveyRepository.save(survey);
        
        try {
            com.pulseai.surveyservice.dto.event.SurveyCompletedEvent event = new com.pulseai.surveyservice.dto.event.SurveyCompletedEvent(surveyId, employeeId, survey.getRegion());
            kafkaTemplate.send("survey-completed", String.valueOf(surveyId), event);
        } catch (Exception e) {
            log.warn("Failed to send survey-completed event: {}", e.getMessage());
        }
    }

    public java.util.List<Survey> getSurveysByStatus(Long employeeId, AssignmentStatus status) {
        java.util.List<SurveyAssignment> assignments = surveyAssignmentRepository.findByEmployeeIdAndStatus(employeeId, status);
        java.util.List<Long> surveyIds = assignments.stream().map(SurveyAssignment::getSurveyId).toList();
        return surveyRepository.findAllById(surveyIds);
    }

    public java.util.Map<String, Object> getSurveyDetails(Long surveyId, Long employeeId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));

        if (survey.getStatus() != SurveyStatus.ACTIVE) {
            throw new BusinessException("Survey is not currently active.");
        }

        SurveyAssignment assignment = surveyAssignmentRepository.findBySurveyIdAndEmployeeId(surveyId, employeeId)
                .orElseThrow(() -> new BusinessException("You are not assigned to this survey."));

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("survey", survey);
        response.put("questions", surveyQuestionRepository.findBySurveyIdOrderByDisplayOrderAsc(surveyId));
        return response;
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SurveyEmployeeService.class);
    private final org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    public SurveyEmployeeService(SurveyRepository surveyRepository, SurveyAssignmentRepository surveyAssignmentRepository, SurveyResponseRepository surveyResponseRepository, AnswerRepository answerRepository, com.pulseai.surveyservice.repository.SurveyQuestionRepository surveyQuestionRepository, org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate) {
        this.surveyRepository = surveyRepository;
        this.surveyAssignmentRepository = surveyAssignmentRepository;
        this.surveyResponseRepository = surveyResponseRepository;
        this.answerRepository = answerRepository;
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
}
