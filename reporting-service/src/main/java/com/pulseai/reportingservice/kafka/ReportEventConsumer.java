package com.pulseai.reportingservice.kafka;

import com.pulseai.reportingservice.dto.event.AIReportGeneratedEvent;
import com.pulseai.reportingservice.service.ReportingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReportEventConsumer {

    private final ReportingService reportingService;

    @KafkaListener(topics = "ai-report-generated-events", groupId = "reporting-service-group")
    public void consumeAiReportGeneratedEvent(AIReportGeneratedEvent event) {
        log.info("Received AIReportGeneratedEvent for Survey ID: {}", event.getSurveyId());
        reportingService.processAiReportGenerated(event);
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReportEventConsumer.class);
    public ReportEventConsumer(ReportingService reportingService) {
        this.reportingService = reportingService;
    }
}
