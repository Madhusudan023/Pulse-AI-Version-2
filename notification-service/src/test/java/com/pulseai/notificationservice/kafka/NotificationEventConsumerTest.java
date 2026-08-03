package com.pulseai.notificationservice.kafka;

import com.pulseai.notificationservice.dto.event.ReportGeneratedEvent;
import com.pulseai.notificationservice.dto.event.SurveyCompletedEvent;
import com.pulseai.notificationservice.dto.event.SurveyPublishedEvent;
import com.pulseai.notificationservice.dto.event.SurveyReminderEvent;
import com.pulseai.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationEventConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationEventConsumer eventConsumer;

    @Test
    void testConsumeSurveyPublished() {
        SurveyPublishedEvent event = new SurveyPublishedEvent();
        event.setSurveyId(1L);

        assertDoesNotThrow(() -> eventConsumer.consumeSurveyPublished(event));

        verify(notificationService, times(1)).processSurveyPublished(event);
    }

    @Test
    void testConsumeSurveyReminder() {
        SurveyReminderEvent event = new SurveyReminderEvent();
        event.setSurveyId(2L);

        assertDoesNotThrow(() -> eventConsumer.consumeSurveyReminder(event));

        verify(notificationService, times(1)).processSurveyReminder(event);
    }

    @Test
    void testConsumeReportGenerated() {
        ReportGeneratedEvent event = new ReportGeneratedEvent();
        event.setReportId(3L);

        assertDoesNotThrow(() -> eventConsumer.consumeReportGenerated(event));

        verify(notificationService, times(1)).processReportGenerated(event);
    }

    @Test
    void testConsumeSurveyCompleted() {
        SurveyCompletedEvent event = new SurveyCompletedEvent();
        event.setSurveyId(4L);
        event.setEmployeeId(5L);

        assertDoesNotThrow(() -> eventConsumer.consumeSurveyCompleted(event));

        verify(notificationService, times(1)).processSurveyCompleted(event);
    }
}
