package com.pulseai.surveyservice.service;

import com.pulseai.surveyservice.dto.request.AnswerRequest;
import com.pulseai.surveyservice.dto.request.SubmitSurveyRequest;
import com.pulseai.surveyservice.entity.Answer;
import com.pulseai.surveyservice.entity.Survey;
import com.pulseai.surveyservice.entity.SurveyResponse;
import com.pulseai.surveyservice.enums.SurveyStatus;
import com.pulseai.surveyservice.exception.BusinessException;
import com.pulseai.surveyservice.exception.ResourceNotFoundException;
import com.pulseai.surveyservice.repository.AnswerRepository;
import com.pulseai.surveyservice.repository.SurveyRepository;
import com.pulseai.surveyservice.repository.SurveyResponseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InternalSurveyResponseService {

    private static final Logger log = LoggerFactory.getLogger(InternalSurveyResponseService.class);

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final AnswerRepository answerRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InternalSurveyResponseService(
            SurveyRepository surveyRepository,
            SurveyResponseRepository surveyResponseRepository,
            AnswerRepository answerRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.surveyRepository = surveyRepository;
        this.surveyResponseRepository = surveyResponseRepository;
        this.answerRepository = answerRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void submitSurveyInternal(Long surveyId, Long employeeId, SubmitSurveyRequest request) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));

        if (survey.getStatus() != SurveyStatus.ACTIVE) {
            throw new BusinessException("Survey is not currently active.");
        }

        SurveyResponse response = new SurveyResponse();
        response.setSurveyId(surveyId);
        response.setEmployeeId(employeeId); // can be null for anonymous
        response.setResponseDuration(request.getResponseDuration());
        response.setSubmittedAt(LocalDateTime.now());
        SurveyResponse savedResponse = surveyResponseRepository.save(response);

        if (request.getAnswers() != null) {
            for (AnswerRequest ansReq : request.getAnswers()) {
                Answer answer = new Answer();
                answer.setResponseId(savedResponse.getId());
                answer.setQuestionId(ansReq.getQuestionId());
                answer.setRatingAnswer(ansReq.getRatingAnswer());
                answer.setTextAnswer(ansReq.getTextAnswer());
                answer.setOptionAnswer(ansReq.getOptionAnswer());
                answerRepository.save(answer);
            }
        }

        survey.setCompletedParticipants(survey.getCompletedParticipants() + 1);
        surveyRepository.save(survey);

        try {
            com.pulseai.surveyservice.dto.event.SurveyCompletedEvent event = 
                new com.pulseai.surveyservice.dto.event.SurveyCompletedEvent(surveyId, employeeId, survey.getRegion());
            kafkaTemplate.send("survey-completed", String.valueOf(surveyId), event);
            log.info("Sent survey-completed event for survey {}", surveyId);
        } catch (Exception e) {
            log.warn("Failed to send survey-completed event: {}", e.getMessage());
        }
    }
}
