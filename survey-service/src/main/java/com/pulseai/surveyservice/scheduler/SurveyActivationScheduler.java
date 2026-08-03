package com.pulseai.surveyservice.scheduler;

import com.pulseai.surveyservice.entity.Survey;
import com.pulseai.surveyservice.enums.SurveyStatus;
import com.pulseai.surveyservice.repository.SurveyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.kafka.core.KafkaTemplate;

@Component
public class SurveyActivationScheduler {

    private final SurveyRepository surveyRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final com.pulseai.surveyservice.repository.SurveyAssignmentRepository surveyAssignmentRepository;

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    public void activateScheduledSurveys() {
        log.info("Running SurveyActivationScheduler");
        List<Survey> scheduledSurveys = surveyRepository.findByStatus(SurveyStatus.SCHEDULED);
        
        for (Survey survey : scheduledSurveys) {
            if (survey.getStartDate() != null && survey.getStartDate().isBefore(LocalDateTime.now())) {
                survey.setStatus(SurveyStatus.ACTIVE);
                surveyRepository.save(survey);
                log.info("Activated survey: {}", survey.getId());
                
                List<Long> employeeIds = surveyAssignmentRepository.findBySurveyId(survey.getId()).stream()
                        .map(com.pulseai.surveyservice.entity.SurveyAssignment::getEmployeeId)
                        .collect(java.util.stream.Collectors.toList());

                com.pulseai.surveyservice.dto.event.SurveyPublishedEvent event = com.pulseai.surveyservice.dto.event.SurveyPublishedEvent.builder()
                    .surveyId(survey.getId())
                    .region(survey.getRegion())
                    .title(survey.getTitle())
                    .publishedAt(LocalDateTime.now())
                    .employeeIds(employeeIds)
                    .build();
                kafkaTemplate.send("survey-published", String.valueOf(survey.getId()), event);
            }
        }
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SurveyActivationScheduler.class);
    public SurveyActivationScheduler(SurveyRepository surveyRepository, KafkaTemplate<String, Object> kafkaTemplate, com.pulseai.surveyservice.repository.SurveyAssignmentRepository surveyAssignmentRepository) {
        this.surveyRepository = surveyRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.surveyAssignmentRepository = surveyAssignmentRepository;
    }
}
