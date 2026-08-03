package com.pulseai.surveyservice.scheduler;

import com.pulseai.surveyservice.dto.event.SurveyClosedEvent;
import com.pulseai.surveyservice.entity.Survey;
import com.pulseai.surveyservice.enums.SurveyStatus;
import com.pulseai.surveyservice.repository.SurveyRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SurveyClosureScheduler {

    private final SurveyRepository surveyRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String SURVEY_CLOSED_TOPIC = "survey-closed-events";

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void closeExpiredSurveys() {
        log.info("Running SurveyClosureScheduler");
        List<Survey> activeSurveys = surveyRepository.findByStatus(SurveyStatus.ACTIVE);
        
        for (Survey survey : activeSurveys) {
            if (survey.getEndDate() != null && survey.getEndDate().isBefore(LocalDateTime.now())) {
                survey.setStatus(SurveyStatus.CLOSED);
                survey.setClosedAt(LocalDateTime.now());
                surveyRepository.save(survey);
                
                log.info("Closed survey: {}. Triggering Kafka event.", survey.getId());
                
                SurveyClosedEvent event = SurveyClosedEvent.builder()
                        .surveyId(survey.getId())
                        .region(survey.getRegion())
                        .surveyType(survey.getSurveyType())
                        .month(survey.getMonth())
                        .year(survey.getYear())
                        .closedAt(survey.getClosedAt())
                        .build();
                        
                kafkaTemplate.send(SURVEY_CLOSED_TOPIC, String.valueOf(survey.getId()), event);
            }
        }
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SurveyClosureScheduler.class);
    public SurveyClosureScheduler(SurveyRepository surveyRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.surveyRepository = surveyRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
}
