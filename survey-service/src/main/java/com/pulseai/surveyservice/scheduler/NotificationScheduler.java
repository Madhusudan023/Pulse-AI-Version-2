package com.pulseai.surveyservice.scheduler;

import com.pulseai.surveyservice.dto.event.SurveyReminderEvent;
import com.pulseai.surveyservice.entity.Survey;
import com.pulseai.surveyservice.entity.SurveyAssignment;
import com.pulseai.surveyservice.enums.AssignmentStatus;
import com.pulseai.surveyservice.enums.SurveyStatus;
import com.pulseai.surveyservice.repository.SurveyAssignmentRepository;
import com.pulseai.surveyservice.repository.SurveyRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NotificationScheduler {

    private final SurveyRepository surveyRepository;
    private final SurveyAssignmentRepository surveyAssignmentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Run every morning at 8:00 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void sendReminders() {
        log.info("Running NotificationScheduler for survey reminders...");
        List<Survey> activeSurveys = surveyRepository.findByStatus(SurveyStatus.ACTIVE);
        
        for (Survey survey : activeSurveys) {
            List<SurveyAssignment> pendingAssignments = surveyAssignmentRepository.findBySurveyId(survey.getId())
                    .stream()
                    .filter(a -> a.getStatus() == AssignmentStatus.PENDING)
                    .collect(Collectors.toList());
                    
            if (!pendingAssignments.isEmpty()) {
                List<Long> employeeIds = pendingAssignments.stream()
                        .map(SurveyAssignment::getEmployeeId)
                        .collect(Collectors.toList());
                        
                SurveyReminderEvent event = SurveyReminderEvent.builder()
                        .surveyId(survey.getId())
                        .region(survey.getRegion())
                        .title(survey.getTitle())
                        .employeeIds(employeeIds)
                        .build();
                        
                kafkaTemplate.send("survey-reminder", String.valueOf(survey.getId()), event);
                log.info("Sent reminder event for Survey ID {} to {} employees", survey.getId(), employeeIds.size());
            }
        }
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationScheduler.class);
    public NotificationScheduler(SurveyRepository surveyRepository, SurveyAssignmentRepository surveyAssignmentRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.surveyRepository = surveyRepository;
        this.surveyAssignmentRepository = surveyAssignmentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
}
