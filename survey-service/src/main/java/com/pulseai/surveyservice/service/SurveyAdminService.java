package com.pulseai.surveyservice.service;

import com.pulseai.surveyservice.client.EmployeeFeignClient;
import com.pulseai.surveyservice.client.QuestionBankFeignClient;
import com.pulseai.surveyservice.dto.request.AddQuestionsBulkRequest;
import com.pulseai.surveyservice.dto.request.CreateSurveyRequest;
import com.pulseai.surveyservice.entity.Survey;
import com.pulseai.surveyservice.entity.SurveyAssignment;
import com.pulseai.surveyservice.entity.SurveyQuestion;
import com.pulseai.surveyservice.enums.AssignmentStatus;
import com.pulseai.surveyservice.enums.SurveyStatus;
import com.pulseai.surveyservice.exception.BusinessException;
import com.pulseai.surveyservice.exception.ResourceNotFoundException;
import com.pulseai.surveyservice.repository.SurveyAssignmentRepository;
import com.pulseai.surveyservice.repository.SurveyQuestionRepository;
import com.pulseai.surveyservice.repository.SurveyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import com.pulseai.surveyservice.client.SentimentFeignClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SurveyAdminService {

    private final SurveyRepository surveyRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyAssignmentRepository surveyAssignmentRepository;
    private final EmployeeFeignClient employeeFeignClient;
    private final QuestionBankFeignClient questionBankFeignClient;
    private final SentimentFeignClient sentimentFeignClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Survey createSurvey(CreateSurveyRequest request) {
        Survey s = new Survey();
        s.setTitle(request.getTitle());
        s.setDescription(request.getDescription());
        s.setRegion(request.getRegion());
        s.setSurveyType(request.getSurveyType());
        s.setMonth(request.getMonth());
        s.setYear(request.getYear());
        s.setStartDate(request.getStartDate());
        s.setEndDate(request.getEndDate());
        s.setTargetAudience(request.getTargetAudience());
        s.setAnonymous(request.isAnonymous());
        s.setStatus(SurveyStatus.DRAFT);
        s = surveyRepository.save(s);
        
        if (s.getSurveyType() != null && s.getSurveyType().name().endsWith("_MONTH_SURVEY")) {
            try {
                java.util.List<com.pulseai.surveyservice.dto.response.QuestionResponseDTO> templateQuestions = questionBankFeignClient.getApprovedQuestions(s.getRegion(), s.getSurveyType().name());
                if (templateQuestions != null && !templateQuestions.isEmpty()) {
                    for (int i = 0; i < templateQuestions.size(); i++) {
                        SurveyQuestion sq = new SurveyQuestion();
                        sq.setSurveyId(s.getId());
                        sq.setQuestionId(templateQuestions.get(i).getId());
                        sq.setDisplayOrder(i + 1);
                        surveyQuestionRepository.save(sq);
                    }
                    log.info("Auto-populated {} questions for template survey {}", templateQuestions.size(), s.getId());
                }
            } catch (Exception e) {
                log.warn("Failed to auto-populate questions for template survey: {}", e.getMessage());
            }
        }
        return s;
    }

    @Transactional
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "question-bank-service", fallbackMethod = "addQuestionsBulkFallback")
    @io.github.resilience4j.retry.annotation.Retry(name = "question-bank-service")
    public void addQuestionsBulk(Long surveyId, AddQuestionsBulkRequest request) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));

        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BusinessException("Cannot add questions. Survey is not in DRAFT status.");
        }

        int displayOrder = 1;
        for (Long qId : request.getQuestionIds()) {
            // Validate question exists
            questionBankFeignClient.getQuestionById(qId);
            
            SurveyQuestion sq = new SurveyQuestion();
            sq.setSurveyId(surveyId);
            sq.setQuestionId(qId);
            sq.setDisplayOrder(displayOrder++);
            surveyQuestionRepository.save(sq);
        }
    }

    @Transactional
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "employee-service", fallbackMethod = "publishSurveyFallback")
    @io.github.resilience4j.retry.annotation.Retry(name = "employee-service")
    public void publishSurvey(Long surveyId, List<String> customEmails) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));

        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BusinessException("Can only publish DRAFT surveys");
        }

        // Fetch eligible employees using Employee Service (internal API)
        java.util.List<com.pulseai.surveyservice.dto.response.EmployeeInternalResponse> employees;
        if ("NEW_JOINERS".equals(survey.getTargetAudience())) {
            employees = employeeFeignClient.getNewJoinersByRegion(survey.getRegion());
        } else if ("TENURED".equals(survey.getTargetAudience())) {
            employees = employeeFeignClient.getTenuredEmployeesByRegion(survey.getRegion());
        } else {
            employees = employeeFeignClient.getEmployeesByRegion(survey.getRegion());
        }
        
        for (var emp : employees) {
            if (surveyAssignmentRepository.findBySurveyIdAndEmployeeId(surveyId, emp.getEmployeeId()).isEmpty()) {
                SurveyAssignment sa = new SurveyAssignment();
                sa.setSurveyId(surveyId);
                sa.setEmployeeId(emp.getEmployeeId());
                sa.setStatus(AssignmentStatus.PENDING);
                sa.setAssignedAt(LocalDateTime.now());
                surveyAssignmentRepository.save(sa);
            }
        }

        long assignmentCount = surveyAssignmentRepository.findBySurveyId(surveyId).size();
        survey.setExpectedParticipants((int) assignmentCount + (customEmails != null ? customEmails.size() : 0));
        survey.setPublishedAt(LocalDateTime.now());
        
        // Either scheduled or active based on start date
        if (survey.getStartDate().isAfter(LocalDateTime.now())) {
            survey.setStatus(SurveyStatus.SCHEDULED);
        } else {
            survey.setStatus(SurveyStatus.ACTIVE);
            
            String experienceFilter = "ALL";
            if ("NEW_JOINERS".equals(survey.getTargetAudience())) {
                experienceFilter = "LESS_THAN_6_MONTHS";
            } else if ("TENURED".equals(survey.getTargetAudience())) {
                experienceFilter = "MORE_THAN_6_MONTHS";
            }

            // Emit survey-published event immediately if active
            com.pulseai.surveyservice.dto.event.SurveyPublishedEvent event = com.pulseai.surveyservice.dto.event.SurveyPublishedEvent.builder()
                .surveyId(surveyId)
                .region(survey.getRegion())
                .title(survey.getTitle())
                .publishedAt(survey.getPublishedAt())
                .customEmails(customEmails)
                .employeeIds(employees.stream().map(com.pulseai.surveyservice.dto.response.EmployeeInternalResponse::getEmployeeId).collect(java.util.stream.Collectors.toList()))
                .experienceFilter(experienceFilter)
                .build();
            try { kafkaTemplate.send("survey-published", String.valueOf(surveyId), event); } catch (Exception e) { log.warn("Failed to send survey-published Kafka event. Is Kafka running? Error: {}", e.getMessage()); }
        }
        
        surveyRepository.save(survey);
        log.info("Survey {} published. Created {} assignments.", surveyId, employees.size());
    }

    @Transactional
    public void closeSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));

        survey.setStatus(SurveyStatus.CLOSED);
        survey.setClosedAt(LocalDateTime.now());
        surveyRepository.save(survey);
        
        com.pulseai.surveyservice.dto.event.SurveyClosedEvent event = new com.pulseai.surveyservice.dto.event.SurveyClosedEvent();
        event.setSurveyId(surveyId);
        event.setRegion(survey.getRegion());
        event.setSurveyType(survey.getSurveyType());
        event.setMonth(survey.getMonth());
        event.setYear(survey.getYear());
        event.setClosedAt(survey.getClosedAt());
        
        try {
            kafkaTemplate.send("survey-closed-events", String.valueOf(surveyId), event);
            log.info("Survey {} closed. Emitted SurveyClosedEvent.", surveyId);
        } catch (Exception e) {
            log.warn("Failed to send Kafka event for closed survey {}. Falling back to OpenFeign. Error: {}", surveyId, e.getMessage());
            sentimentFeignClient.triggerAnalysis(event);
            log.info("Survey {} closed via synchronous OpenFeign fallback.", surveyId);
        }
    }

    @Transactional
    public void reactivateSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));

        if (survey.getStatus() != SurveyStatus.CLOSED && survey.getStatus() != SurveyStatus.ARCHIVED 
                && survey.getStatus() != SurveyStatus.ACTIVE && survey.getStatus() != SurveyStatus.SCHEDULED) {
            throw new BusinessException("Survey must be CLOSED, ARCHIVED, ACTIVE, or SCHEDULED to be reactivated");
        }

        survey.setStatus(SurveyStatus.DRAFT);
        surveyRepository.save(survey);

        log.info("Survey {} reactivated to DRAFT status for editing.", surveyId);
    }

    @Transactional
    public Survey updateSurvey(Long surveyId, CreateSurveyRequest request) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));

        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BusinessException("Only DRAFT surveys can be edited");
        }

        survey.setTitle(request.getTitle());
        survey.setDescription(request.getDescription());
        survey.setSurveyType(request.getSurveyType());
        survey.setTargetAudience(request.getTargetAudience());
        survey.setMonth(request.getMonth());
        survey.setYear(request.getYear());
        survey.setStartDate(request.getStartDate());
        survey.setEndDate(request.getEndDate());
        return surveyRepository.save(survey);
    }

    @Transactional
    public void deleteSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));
                
        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BusinessException("Only DRAFT surveys can be deleted");
        }
        
        surveyAssignmentRepository.deleteAll(surveyAssignmentRepository.findBySurveyId(surveyId));
        surveyQuestionRepository.deleteAll(surveyQuestionRepository.findBySurveyIdOrderByDisplayOrderAsc(surveyId));
        surveyRepository.delete(survey);
        
        log.info("Survey {} deleted successfully.", surveyId);
    }

    @Transactional
    public void removeQuestionsFromSurvey(Long surveyId, java.util.List<Long> questionIds) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));
        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BusinessException("Questions can only be removed from DRAFT surveys");
        }
        surveyQuestionRepository.deleteBySurveyIdAndQuestionIdIn(surveyId, questionIds);
        log.info("Removed {} question(s) from survey {}.", questionIds.size(), surveyId);
    }


    public List<Survey> getSurveysByRegion(String region, SurveyStatus status) {
        if ("GLOBAL".equalsIgnoreCase(region)) {
            if (status != null) {
                return surveyRepository.findByStatus(status);
            }
            return surveyRepository.findAll();
        }
        if (status != null) {
            return surveyRepository.findByRegionAndStatus(region, status);
        }
        return surveyRepository.findByRegion(region);
    }

    public List<Long> getLastSurveyQuestionIds(com.pulseai.surveyservice.enums.SurveyType type) {
        return surveyRepository.findFirstBySurveyTypeOrderByCreatedAtDesc(type)
                .map(survey -> surveyQuestionRepository.findBySurveyIdOrderByDisplayOrderAsc(survey.getId())
                        .stream()
                        .map(SurveyQuestion::getQuestionId)
                        .collect(java.util.stream.Collectors.toList()))
                .orElse(java.util.Collections.emptyList());
    }

    public List<SurveyQuestion> getSurveyQuestions(Long surveyId) {
        return surveyQuestionRepository.findBySurveyIdOrderByDisplayOrderAsc(surveyId);
    }

    public void addQuestionsBulkFallback(Long surveyId, AddQuestionsBulkRequest request, Throwable t) {
        log.warn("Question Bank Service unavailable. Circuit breaker activated.", t);
        throw new com.pulseai.surveyservice.exception.ServiceUnavailableException("Question Bank Service temporarily unavailable.");
    }

    public void publishSurveyFallback(Long surveyId, Throwable t) {
        log.warn("Employee Service unavailable. Circuit breaker activated.", t);
        throw new com.pulseai.surveyservice.exception.ServiceUnavailableException("Employee Service temporarily unavailable. Cannot fetch participants.");
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SurveyAdminService.class);
    private final com.pulseai.surveyservice.repository.SurveyResponseRepository surveyResponseRepository;
    private final com.pulseai.surveyservice.repository.AnswerRepository answerRepository;

    public SurveyAdminService(SurveyRepository surveyRepository, SurveyQuestionRepository surveyQuestionRepository, SurveyAssignmentRepository surveyAssignmentRepository, EmployeeFeignClient employeeFeignClient, QuestionBankFeignClient questionBankFeignClient, SentimentFeignClient sentimentFeignClient, KafkaTemplate<String, Object> kafkaTemplate, com.pulseai.surveyservice.repository.SurveyResponseRepository surveyResponseRepository, com.pulseai.surveyservice.repository.AnswerRepository answerRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.surveyAssignmentRepository = surveyAssignmentRepository;
        this.employeeFeignClient = employeeFeignClient;
        this.questionBankFeignClient = questionBankFeignClient;
        this.sentimentFeignClient = sentimentFeignClient;
        this.kafkaTemplate = kafkaTemplate;
        this.surveyResponseRepository = surveyResponseRepository;
        this.answerRepository = answerRepository;
    }

    public List<com.pulseai.surveyservice.dto.response.FullSurveyResponseDTO> getNonAnonymousResponses(Long surveyId) {
        List<com.pulseai.surveyservice.entity.SurveyResponse> responses = surveyResponseRepository.findBySurveyId(surveyId);
        return responses.stream()
            .filter(r -> r.getEmployeeEmail() != null)
            .map(r -> {
                com.pulseai.surveyservice.dto.response.FullSurveyResponseDTO dto = new com.pulseai.surveyservice.dto.response.FullSurveyResponseDTO();
                dto.setResponse(r);
                dto.setAnswers(answerRepository.findByResponseId(r.getId()));
                return dto;
            })
            .collect(java.util.stream.Collectors.toList());
    }
}

