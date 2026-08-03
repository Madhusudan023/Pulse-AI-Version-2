package com.pulseai.reportingservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseai.reportingservice.client.SentimentFeignClient;
import com.pulseai.reportingservice.client.SurveyFeignClient;
import com.pulseai.reportingservice.csv.CsvGeneratorService;
import com.pulseai.reportingservice.dto.event.AIReportGeneratedEvent;
import com.pulseai.reportingservice.dto.event.ReportGeneratedEvent;
import com.pulseai.reportingservice.entity.Report;
import com.pulseai.reportingservice.entity.ReportExport;
import com.pulseai.reportingservice.entity.ReportRecommendation;
import com.pulseai.reportingservice.entity.ReportTheme;
import com.pulseai.reportingservice.pdf.PdfGeneratorService;
import com.pulseai.reportingservice.repository.ReportRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ReportingService {

    private final SentimentFeignClient sentimentFeignClient;
    private final SurveyFeignClient surveyFeignClient;
    private final ReportRepository reportRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final CsvGeneratorService csvGeneratorService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processAiReportGenerated(AIReportGeneratedEvent event) {
        log.info("Processing AIReportGeneratedEvent for Survey ID: {}", event.getSurveyId());

        // 1. Fetch AI Report
        Map<String, Object> sentimentReport = sentimentFeignClient.getReportBySurveyId(event.getSurveyId());

        // 2. Fetch Survey Context for Participation Rate
        Map<String, Object> surveyContext = surveyFeignClient.getSurveyContext(event.getSurveyId());
        
        Integer expected = (Integer) surveyContext.get("expectedParticipants");
        Integer completed = (Integer) surveyContext.get("completedParticipants");
        double participationRate = expected > 0 ? ((double) completed / expected) * 100 : 0.0;

        // 3. Create CQRS Read Model
        Report report = new Report();
        report.setSurveyId(event.getSurveyId());
        report.setRegion(event.getRegion());
        report.setMonth(event.getMonth());
        report.setYear(event.getYear());
        report.setOverallScore((Integer) sentimentReport.get("overallScore"));
        report.setPositivePercentage((Integer) sentimentReport.get("positivePercentage"));
        report.setNeutralPercentage((Integer) sentimentReport.get("neutralPercentage"));
        report.setNegativePercentage((Integer) sentimentReport.get("negativePercentage"));
        report.setParticipationRate(Math.round(participationRate * 10.0) / 10.0);
        report.setExecutiveSummary((String) sentimentReport.get("executiveSummary"));
        report.setGeneratedAt(LocalDateTime.now());

        try {
            // Parse Themes
            List<String> posThemes = objectMapper.readValue((String) sentimentReport.get("positiveThemes"), new TypeReference<List<String>>(){});
            posThemes.forEach(t -> {
                ReportTheme rt = new ReportTheme();
                rt.setReport(report);
                rt.setTheme(t);
                rt.setType("POSITIVE");
                report.getThemes().add(rt);
            });

            List<String> negThemes = objectMapper.readValue((String) sentimentReport.get("negativeThemes"), new TypeReference<List<String>>(){});
            negThemes.forEach(t -> {
                ReportTheme rt = new ReportTheme();
                rt.setReport(report);
                rt.setTheme(t);
                rt.setType("NEGATIVE");
                report.getThemes().add(rt);
            });

            // Parse Recommendations
            // Parse Recommendations
            List<String> recommendations = objectMapper.readValue((String) sentimentReport.get("recommendations"), new TypeReference<List<String>>(){});
            recommendations.forEach(r -> {
                ReportRecommendation rr = new ReportRecommendation();
                rr.setReport(report);
                rr.setRecommendation(r);
                report.getRecommendations().add(rr);
            });
            
            // Parse Question Analysis
            String qaJson = (String) sentimentReport.get("questionWiseAnalysis");
            if (qaJson != null) {
                List<Map<String, Object>> qas = objectMapper.readValue(qaJson, new TypeReference<List<Map<String, Object>>>(){});
                qas.forEach(qaMap -> {
                    com.pulseai.reportingservice.entity.ReportQuestionAnalysis qa = new com.pulseai.reportingservice.entity.ReportQuestionAnalysis();
                    qa.setReport(report);
                    qa.setQuestionText((String) qaMap.get("questionText"));
                    qa.setPositivePercentage((Integer) qaMap.get("positivePercentage"));
                    qa.setNeutralPercentage((Integer) qaMap.get("neutralPercentage"));
                    qa.setNegativePercentage((Integer) qaMap.get("negativePercentage"));
                    qa.setSummary((String) qaMap.get("summary"));
                    report.getQuestionAnalysis().add(qa);
                });
            }
        } catch (Exception e) {
            log.error("Failed to parse JSON arrays from SentimentReport", e);
        }

        // Save report to get ID
        Report savedReport = reportRepository.save(report);

        // 4. Generate Exports
        String pdfPath = pdfGeneratorService.generatePdf(savedReport);
        String csvPath = csvGeneratorService.generateCsv(savedReport);

        ReportExport export = new ReportExport();
        export.setReport(savedReport);
        export.setPdfPath(pdfPath);
        export.setCsvPath(csvPath);
        export.setGeneratedAt(LocalDateTime.now());
        savedReport.setExport(export);

        reportRepository.save(savedReport);

        // 5. Publish Event
        ReportGeneratedEvent outEvent = ReportGeneratedEvent.builder()
                .reportId(savedReport.getId())
                .surveyId(savedReport.getSurveyId())
                .region(savedReport.getRegion())
                .month(savedReport.getMonth())
                .year(savedReport.getYear())
                .generatedAt(savedReport.getGeneratedAt())
                .build();
                
        kafkaTemplate.send("report-generated", String.valueOf(savedReport.getSurveyId()), outEvent);
        log.info("Finished processing Reporting for Survey {}, PDF: {}", event.getSurveyId(), pdfPath);
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReportingService.class);
    public ReportingService(SentimentFeignClient sentimentFeignClient, SurveyFeignClient surveyFeignClient, ReportRepository reportRepository, PdfGeneratorService pdfGeneratorService, CsvGeneratorService csvGeneratorService, KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.sentimentFeignClient = sentimentFeignClient;
        this.surveyFeignClient = surveyFeignClient;
        this.reportRepository = reportRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.csvGeneratorService = csvGeneratorService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
}
