package com.pulseai.notificationservice.kafka;

import com.pulseai.notificationservice.dto.event.SurveyCompletedEvent;
import com.pulseai.notificationservice.dto.event.ReportGeneratedEvent;
import com.pulseai.notificationservice.dto.event.SurveyPublishedEvent;
import com.pulseai.notificationservice.dto.event.SurveyReminderEvent;
import com.pulseai.notificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "survey-published", groupId = "notification-service-group")
    public void consumeSurveyPublished(SurveyPublishedEvent event) {
        log.info("Received SurveyPublishedEvent for Survey ID: {}", event.getSurveyId());
        notificationService.processSurveyPublished(event);
    }

    @KafkaListener(topics = "survey-reminder", groupId = "notification-service-group")
    public void consumeSurveyReminder(SurveyReminderEvent event) {
        log.info("Received SurveyReminderEvent for Survey ID: {}", event.getSurveyId());
        notificationService.processSurveyReminder(event);
    }

    @KafkaListener(topics = "report-generated", groupId = "notification-service-group")
    public void consumeReportGenerated(ReportGeneratedEvent event) {
        log.info("Received ReportGeneratedEvent for Report ID: {}", event.getReportId());
        notificationService.processReportGenerated(event);
    }

    @KafkaListener(topics = "survey-completed", groupId = "notification-service-group")
    public void consumeSurveyCompleted(SurveyCompletedEvent event) {
        log.info("Received SurveyCompletedEvent for Survey ID: {}, Employee: {}", event.getSurveyId(), event.getEmployeeId());
        notificationService.processSurveyCompleted(event);
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationEventConsumer.class);
    public NotificationEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
