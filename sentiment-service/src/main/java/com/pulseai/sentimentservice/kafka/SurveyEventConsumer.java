package com.pulseai.sentimentservice.kafka;

import com.pulseai.sentimentservice.dto.event.SurveyClosedEvent;
import com.pulseai.sentimentservice.service.SentimentAnalysisService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SurveyEventConsumer {

    private final SentimentAnalysisService sentimentAnalysisService;

    @org.springframework.kafka.annotation.RetryableTopic(
        attempts = "3",
        dltStrategy = org.springframework.kafka.retrytopic.DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = "survey-closed-events", groupId = "sentiment-service-group")
    public void consumeSurveyClosedEvent(SurveyClosedEvent event) {
        log.info("Received SurveyClosedEvent for survey ID: {}", event.getSurveyId());
        sentimentAnalysisService.processSurveyClosedEvent(event);
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SurveyEventConsumer.class);
    public SurveyEventConsumer(SentimentAnalysisService sentimentAnalysisService) {
        this.sentimentAnalysisService = sentimentAnalysisService;
    }
}
