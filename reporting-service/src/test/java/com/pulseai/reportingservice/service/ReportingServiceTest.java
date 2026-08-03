package com.pulseai.reportingservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseai.reportingservice.client.SentimentFeignClient;
import com.pulseai.reportingservice.client.SurveyFeignClient;
import com.pulseai.reportingservice.csv.CsvGeneratorService;
import com.pulseai.reportingservice.dto.event.AIReportGeneratedEvent;
import com.pulseai.reportingservice.dto.event.ReportGeneratedEvent;
import com.pulseai.reportingservice.entity.Report;
import com.pulseai.reportingservice.pdf.PdfGeneratorService;
import com.pulseai.reportingservice.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportingServiceTest {

    @Mock private SentimentFeignClient sentimentFeignClient;
    @Mock private SurveyFeignClient surveyFeignClient;
    @Mock private ReportRepository reportRepository;
    @Mock private PdfGeneratorService pdfGeneratorService;
    @Mock private CsvGeneratorService csvGeneratorService;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private ReportingService reportingService;

    private AIReportGeneratedEvent aiEvent;
    private Map<String, Object> mockSentimentResponse;
    private Map<String, Object> mockSurveyContext;

    @BeforeEach
    void setUp() throws Exception {
        aiEvent = new AIReportGeneratedEvent();
        aiEvent.setSurveyId(1L);
        aiEvent.setRegion("GLOBAL");
        aiEvent.setMonth(8);
        aiEvent.setYear(2026);

        mockSentimentResponse = new HashMap<>();
        mockSentimentResponse.put("overallScore", 85);
        mockSentimentResponse.put("positivePercentage", 80);
        mockSentimentResponse.put("neutralPercentage", 15);
        mockSentimentResponse.put("negativePercentage", 5);
        mockSentimentResponse.put("executiveSummary", "Good performance.");
        mockSentimentResponse.put("positiveThemes", "[\"Growth\"]");
        mockSentimentResponse.put("negativeThemes", "[\"Workload\"]");
        mockSentimentResponse.put("recommendations", "[\"Hire more\"]");
        mockSentimentResponse.put("questionWiseAnalysis", "[{\"questionText\":\"Q1\",\"positivePercentage\":80,\"neutralPercentage\":10,\"negativePercentage\":10,\"summary\":\"Good\"}]");

        mockSurveyContext = new HashMap<>();
        mockSurveyContext.put("expectedParticipants", 100);
        mockSurveyContext.put("completedParticipants", 75);

        lenient().when(sentimentFeignClient.getReportBySurveyId(1L)).thenReturn(mockSentimentResponse);
        lenient().when(surveyFeignClient.getSurveyContext(1L)).thenReturn(mockSurveyContext);
        
        lenient().when(objectMapper.readValue(eq("[\"Growth\"]"), any(TypeReference.class)))
                 .thenReturn(Arrays.asList("Growth"));
        lenient().when(objectMapper.readValue(eq("[\"Workload\"]"), any(TypeReference.class)))
                 .thenReturn(Arrays.asList("Workload"));
        lenient().when(objectMapper.readValue(eq("[\"Hire more\"]"), any(TypeReference.class)))
                 .thenReturn(Arrays.asList("Hire more"));

        // Setup mock for QA
        Map<String, Object> qaMap = new HashMap<>();
        qaMap.put("questionText", "Q1");
        qaMap.put("positivePercentage", 80);
        qaMap.put("neutralPercentage", 10);
        qaMap.put("negativePercentage", 10);
        qaMap.put("summary", "Good");
        lenient().when(objectMapper.readValue(eq((String)mockSentimentResponse.get("questionWiseAnalysis")), any(TypeReference.class)))
                 .thenReturn(Arrays.asList(qaMap));

        lenient().when(reportRepository.save(any(Report.class))).thenAnswer(i -> {
            Report r = i.getArgument(0);
            if (r.getId() == null) r.setId(10L);
            return r;
        });

        lenient().when(pdfGeneratorService.generatePdf(any())).thenReturn("/exports/1.pdf");
        lenient().when(csvGeneratorService.generateCsv(any())).thenReturn("/exports/1.csv");
    }

    // --- 1. Report Generation Tests ---

    @Test
    void processAiReportGenerated_Success() {
        reportingService.processAiReportGenerated(aiEvent);

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository, atLeastOnce()).save(reportCaptor.capture());
        
        Report savedReport = reportCaptor.getValue();
        assertEquals(1L, savedReport.getSurveyId());
        assertEquals(85, savedReport.getOverallScore());
        assertEquals(80, savedReport.getPositivePercentage());
        assertEquals("Good performance.", savedReport.getExecutiveSummary());

        // Assert 75% participation
        assertEquals(75.0, savedReport.getParticipationRate());
        
        // Assert Export saved
        assertNotNull(savedReport.getExport());
        assertEquals("/exports/1.pdf", savedReport.getExport().getPdfPath());

        // Assert Themes & QA parsed
        assertFalse(savedReport.getThemes().isEmpty());
        assertFalse(savedReport.getRecommendations().isEmpty());
        assertFalse(savedReport.getQuestionAnalysis().isEmpty());

        // Assert Kafka Event
        ArgumentCaptor<ReportGeneratedEvent> eventCaptor = ArgumentCaptor.forClass(ReportGeneratedEvent.class);
        verify(kafkaTemplate).send(eq("report-generated"), anyString(), eventCaptor.capture());
        assertEquals(10L, eventCaptor.getValue().getReportId());
    }

    // --- 2. Participation Calculation Tests ---

    @Test
    void processAiReportGenerated_ZeroParticipation() {
        mockSurveyContext.put("expectedParticipants", 100);
        mockSurveyContext.put("completedParticipants", 0);
        
        reportingService.processAiReportGenerated(aiEvent);

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository, atLeastOnce()).save(reportCaptor.capture());
        
        assertEquals(0.0, reportCaptor.getValue().getParticipationRate());
    }

    @Test
    void processAiReportGenerated_100PercentParticipation() {
        mockSurveyContext.put("expectedParticipants", 50);
        mockSurveyContext.put("completedParticipants", 50);
        
        reportingService.processAiReportGenerated(aiEvent);

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository, atLeastOnce()).save(reportCaptor.capture());
        
        assertEquals(100.0, reportCaptor.getValue().getParticipationRate());
    }

    // --- 20. AI Failure Handling ---

    @Test
    void processAiReportGenerated_InvalidJsonHandling() throws Exception {
        // Simulating bad JSON array from Gemini
        mockSentimentResponse.put("positiveThemes", "invalid_json");
        when(objectMapper.readValue(eq("invalid_json"), any(TypeReference.class)))
                .thenThrow(new RuntimeException("JSON Parse error"));

        // Should not crash the process, just skip themes
        assertDoesNotThrow(() -> reportingService.processAiReportGenerated(aiEvent));

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository, atLeastOnce()).save(reportCaptor.capture());

        // Basic data should still be present
        Report savedReport = reportCaptor.getValue();
        assertEquals(85, savedReport.getOverallScore());
        verify(kafkaTemplate, times(1)).send(eq("report-generated"), anyString(), any());
    }

    @Test
    void processAiReportGenerated_MissingQuestionAnalysis() {
        mockSentimentResponse.remove("questionWiseAnalysis");

        assertDoesNotThrow(() -> reportingService.processAiReportGenerated(aiEvent));

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository, atLeastOnce()).save(reportCaptor.capture());
        assertTrue(reportCaptor.getValue().getQuestionAnalysis().isEmpty());
    }
}
