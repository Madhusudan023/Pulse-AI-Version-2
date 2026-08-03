package com.pulseai.sentimentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseai.sentimentservice.client.GeminiClient;
import com.pulseai.sentimentservice.client.QuestionBankFeignClient;
import com.pulseai.sentimentservice.client.ReportingFeignClient;
import com.pulseai.sentimentservice.client.SurveyFeignClient;
import com.pulseai.sentimentservice.dto.event.AIReportGeneratedEvent;
import com.pulseai.sentimentservice.dto.event.SurveyClosedEvent;
import com.pulseai.sentimentservice.dto.response.SentimentAnalysisResult;
import com.pulseai.sentimentservice.dto.response.SentimentAnalysisResult.SuggestedQuestion;
import com.pulseai.sentimentservice.entity.AiProcessingLog;
import com.pulseai.sentimentservice.entity.SentimentReport;
import com.pulseai.sentimentservice.parser.GeminiResponseParser;
import com.pulseai.sentimentservice.prompt.PromptBuilder;
import com.pulseai.sentimentservice.repository.AiProcessingLogRepository;
import com.pulseai.sentimentservice.repository.SentimentReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SentimentAnalysisServiceTest {

    @Mock private SurveyFeignClient surveyFeignClient;
    @Mock private QuestionBankFeignClient questionBankFeignClient;
    @Mock private GeminiClient geminiClient;
    @Mock private PromptBuilder promptBuilder;
    @Mock private GeminiResponseParser responseParser;
    @Mock private SentimentReportRepository sentimentReportRepository;
    @Mock private AiProcessingLogRepository aiProcessingLogRepository;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock private ReportingFeignClient reportingFeignClient;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private SentimentAnalysisService sentimentAnalysisService;

    private SurveyClosedEvent closedEvent;
    private SentimentAnalysisResult mockResult;

    @BeforeEach
    void setUp() throws Exception {
        closedEvent = new SurveyClosedEvent();
        closedEvent.setSurveyId(10L);
        closedEvent.setRegion("US");
        closedEvent.setMonth(8);
        closedEvent.setYear(2026);

        mockResult = new SentimentAnalysisResult();
        mockResult.setOverallScore(80);
        mockResult.setPositivePercentage(70);
        mockResult.setNeutralPercentage(20);
        mockResult.setNegativePercentage(10);
        mockResult.setExecutiveSummary("Good results");
        
        SuggestedQuestion sq = new SuggestedQuestion();
        sq.setQuestionText("How can we improve?");
        mockResult.setSuggestedQuestions(Collections.singletonList(sq));

        lenient().when(surveyFeignClient.getSurveyContext(10L)).thenReturn(new HashMap<>());
        lenient().when(surveyFeignClient.getSurveyResponses(10L)).thenReturn(Collections.emptyList());
        lenient().when(promptBuilder.buildPrompt(any(), any())).thenReturn("Mock Prompt");
        
        Map<String, Object> geminiResp = new HashMap<>();
        geminiResp.put("text", "VALID AI RESPONSE");
        lenient().when(geminiClient.generateContent(anyString())).thenReturn(geminiResp);
        
        lenient().when(responseParser.parse(anyString())).thenReturn(mockResult);

        lenient().when(sentimentReportRepository.save(any(SentimentReport.class))).thenAnswer(i -> {
            SentimentReport r = i.getArgument(0);
            r.setId(500L);
            return r;
        });
        
        lenient().when(objectMapper.writeValueAsString(any())).thenReturn("[]");
    }

    // --- 1. Survey Event Processing & 13. Kafka Publishing ---

    @Test
    void processSurveyClosedEvent_Success_GeneratesReportAndEvent() {
        sentimentAnalysisService.processSurveyClosedEvent(closedEvent);

        // Verify Logging
        ArgumentCaptor<AiProcessingLog> logCaptor = ArgumentCaptor.forClass(AiProcessingLog.class);
        verify(aiProcessingLogRepository, times(2)).save(logCaptor.capture());
        assertEquals("COMPLETED", logCaptor.getAllValues().get(1).getStatus());

        // Verify Report Saved
        ArgumentCaptor<SentimentReport> reportCaptor = ArgumentCaptor.forClass(SentimentReport.class);
        verify(sentimentReportRepository).save(reportCaptor.capture());
        assertEquals(80, reportCaptor.getValue().getOverallScore());
        assertEquals("Good results", reportCaptor.getValue().getExecutiveSummary());

        // Verify Status Updated
        verify(surveyFeignClient).updateSurveyStatus(10L, "ARCHIVED");

        // Verify Draft Questions Pushed
        verify(questionBankFeignClient).addAiDraftQuestions(anyList());

        // Verify Kafka Event Published
        ArgumentCaptor<AIReportGeneratedEvent> eventCaptor = ArgumentCaptor.forClass(AIReportGeneratedEvent.class);
        verify(kafkaTemplate).send(eq("ai-report-generated-events"), anyString(), eventCaptor.capture());
        assertEquals(500L, eventCaptor.getValue().getReportId());
    }

    // --- 16. AI Failure & 14. Retry Mechanism ---

    @Test
    void processSurveyClosedEvent_GeminiFailure_LogsErrorAndThrows() {
        when(geminiClient.generateContent(anyString())).thenThrow(new RuntimeException("Gemini API Rate Limited"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sentimentAnalysisService.processSurveyClosedEvent(closedEvent);
        });
        assertTrue(exception.getMessage().contains("AI Processing failed"));

        // Verify Error Logging
        ArgumentCaptor<AiProcessingLog> logCaptor = ArgumentCaptor.forClass(AiProcessingLog.class);
        verify(aiProcessingLogRepository, times(2)).save(logCaptor.capture());
        assertEquals("FAILED", logCaptor.getAllValues().get(1).getStatus());
        assertTrue(logCaptor.getAllValues().get(1).getErrorMessage().contains("Gemini API Rate Limited"));

        // Verify no report saved
        verify(sentimentReportRepository, never()).save(any());
        verify(surveyFeignClient, never()).updateSurveyStatus(anyLong(), anyString());
    }

    // --- 15. Fallback Mechanism (Kafka Down) ---

    @Test
    void processSurveyClosedEvent_KafkaDown_OpenFeignFallback() {
        // Force Kafka failure
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenThrow(new RuntimeException("Kafka Broker Down"));

        // Should not throw, should fall back to Feign
        assertDoesNotThrow(() -> sentimentAnalysisService.processSurveyClosedEvent(closedEvent));

        // Verify Fallback used
        verify(reportingFeignClient, times(1)).saveReport(any(AIReportGeneratedEvent.class));
    }
}
