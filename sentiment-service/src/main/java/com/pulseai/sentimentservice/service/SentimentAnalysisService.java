package com.pulseai.sentimentservice.service;

import com.pulseai.sentimentservice.client.GeminiClient;
import com.pulseai.sentimentservice.client.QuestionBankFeignClient;
import com.pulseai.sentimentservice.client.SurveyFeignClient;
import com.pulseai.sentimentservice.client.ReportingFeignClient;
import com.pulseai.sentimentservice.dto.event.AIReportGeneratedEvent;
import com.pulseai.sentimentservice.dto.event.SurveyClosedEvent;
import com.pulseai.sentimentservice.dto.request.CreateQuestionRequest;
import com.pulseai.sentimentservice.dto.response.SentimentAnalysisResult;
import com.pulseai.sentimentservice.entity.AiProcessingLog;
import com.pulseai.sentimentservice.entity.SentimentReport;
import com.pulseai.sentimentservice.parser.GeminiResponseParser;
import com.pulseai.sentimentservice.prompt.PromptBuilder;
import com.pulseai.sentimentservice.repository.AiProcessingLogRepository;
import com.pulseai.sentimentservice.repository.SentimentReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SentimentAnalysisService {

    private final SurveyFeignClient surveyFeignClient;
    private final QuestionBankFeignClient questionBankFeignClient;
    private final GeminiClient geminiClient;
    private final PromptBuilder promptBuilder;
    private final GeminiResponseParser responseParser;
    private final SentimentReportRepository sentimentReportRepository;
    private final AiProcessingLogRepository aiProcessingLogRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReportingFeignClient reportingFeignClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processSurveyClosedEvent(SurveyClosedEvent event) {
        log.info("Starting AI Analysis for Survey ID: {}", event.getSurveyId());
        long startTime = System.currentTimeMillis();

        AiProcessingLog processingLog = new AiProcessingLog();
        processingLog.setSurveyId(event.getSurveyId());
        processingLog.setStatus("STARTED");
        processingLog.setStartedAt(LocalDateTime.now());
        aiProcessingLogRepository.save(processingLog);

        try {
            // 1. Fetch Data
            Map<String, Object> surveyContext = surveyFeignClient.getSurveyContext(event.getSurveyId());
            List<Map<String, Object>> responses = surveyFeignClient.getSurveyResponses(event.getSurveyId());
            
            // Extract question IDs from responses and fetch actual texts
            java.util.Set<Long> questionIds = new java.util.HashSet<>();
            for (Map<String, Object> res : responses) {
                if (res.containsKey("answers")) {
                    List<Map<String, Object>> answers = (List<Map<String, Object>>) res.get("answers");
                    for (Map<String, Object> ans : answers) {
                        if (ans.containsKey("questionId")) {
                            questionIds.add(((Number) ans.get("questionId")).longValue());
                        }
                    }
                }
            }
            List<Map<String, Object>> questionTexts = questionBankFeignClient.getQuestionsByIds(new java.util.ArrayList<>(questionIds));

            // Map question texts into the prompt
            Map<String, Object> enhancedContext = new java.util.HashMap<>(surveyContext);
            enhancedContext.put("questions", questionTexts);

            // 2. Build Prompt
            String prompt = promptBuilder.buildPrompt(enhancedContext, responses);

            // 3. Call Gemini
            Map<String, Object> aiResult = geminiClient.generateContent(prompt);
            String rawAiResponse = (String) aiResult.get("text");
            Object promptTokens = aiResult.get("promptTokenCount");
            Object candidateTokens = aiResult.get("candidatesTokenCount");
            
            log.info("Gemini Tokens - Prompt: {}, Candidate: {}", promptTokens, candidateTokens);

            // 4. Parse Response
            if (rawAiResponse != null && rawAiResponse.contains("MOCKED AI RESPONSE")) {
                rawAiResponse = generateDynamicMockResponse(responses);
            }
            SentimentAnalysisResult result = responseParser.parse(rawAiResponse);

            long duration = System.currentTimeMillis() - startTime;

            // 5. Save Report
            SentimentReport report = new SentimentReport();
            report.setSurveyId(event.getSurveyId());
            report.setRegion(event.getRegion());
            report.setMonth(event.getMonth());
            report.setYear(event.getYear());
            report.setOverallScore(result.getOverallScore());
            report.setPositivePercentage(result.getPositivePercentage());
            report.setNeutralPercentage(result.getNeutralPercentage());
            report.setNegativePercentage(result.getNegativePercentage());
            report.setExecutiveSummary(result.getExecutiveSummary());
            report.setPositiveThemes(objectMapper.writeValueAsString(result.getPositiveThemes()));
            report.setNegativeThemes(objectMapper.writeValueAsString(result.getNegativeThemes()));
            report.setRecommendations(objectMapper.writeValueAsString(result.getRecommendations()));
            report.setQuestionWiseAnalysis(objectMapper.writeValueAsString(result.getQuestionWiseAnalysis()));
            report.setRawAiResponse(rawAiResponse);
            report.setProcessingTime(duration);
            report.setGeneratedAt(LocalDateTime.now());
            
            SentimentReport savedReport = sentimentReportRepository.save(report);

            // 6. Push Draft Questions to Question Bank
            if (result.getSuggestedQuestions() != null && !result.getSuggestedQuestions().isEmpty()) {
                List<CreateQuestionRequest> drafts = result.getSuggestedQuestions().stream().map(sq -> {
                    return CreateQuestionRequest.builder()
                            .questionText(sq.getQuestionText())
                            .category("CULTURE")
                            .questionType("MCQ")
                            .region(event.getRegion())
                            .month(event.getMonth() == 12 ? 1 : event.getMonth() + 1) // next month
                            .year(event.getMonth() == 12 ? event.getYear() + 1 : event.getYear())
                            .surveyType(event.getSurveyType())
                            .remarks("AI Generated based on negative themes from Survey " + event.getSurveyId())
                            .build();
                }).collect(Collectors.toList());
                
                questionBankFeignClient.addAiDraftQuestions(drafts);
                log.info("Pushed {} AI draft questions to Question Bank", drafts.size());
            }

            // 7. Publish Event for Reporting Service
            AIReportGeneratedEvent reportEvent = AIReportGeneratedEvent.builder()
                    .reportId(savedReport.getId())
                    .surveyId(event.getSurveyId())
                    .region(event.getRegion())
                    .month(event.getMonth())
                    .year(event.getYear())
                    .generatedAt(LocalDateTime.now())
                    .build();
            try {
                kafkaTemplate.send("ai-report-generated-events", String.valueOf(reportEvent.getSurveyId()), reportEvent);
            } catch (Exception e) {
                log.warn("Failed to publish ai-report-generated-events to Kafka. Falling back to OpenFeign. Error: {}", e.getMessage());
                reportingFeignClient.saveReport(reportEvent);
                log.info("Sent AIReportGeneratedEvent to reporting-service via synchronous OpenFeign fallback.");
            }
            
            // 8. Update Survey Status to ARCHIVED
            surveyFeignClient.updateSurveyStatus(event.getSurveyId(), "ARCHIVED");
            
            processingLog.setStatus("COMPLETED");
            processingLog.setCompletedAt(LocalDateTime.now());
            aiProcessingLogRepository.save(processingLog);
            
            log.info("AI Analysis completed successfully in {} ms", duration);

        } catch (Exception e) {
            log.error("AI Analysis failed for Survey ID: {}", event.getSurveyId(), e);
            processingLog.setStatus("FAILED");
            processingLog.setErrorMessage(e.getMessage());
            processingLog.setCompletedAt(LocalDateTime.now());
            aiProcessingLogRepository.save(processingLog);
            
            try {
                java.io.FileWriter fw = new java.io.FileWriter("C:/tmp/sentiment_error.log", true);
                java.io.PrintWriter pw = new java.io.PrintWriter(fw);
                e.printStackTrace(pw);
                pw.close();
            } catch (Exception ex) {}

            // Re-throw if we want Kafka consumer to retry (dead-letter queue)
            throw new RuntimeException("AI Processing failed", e);
        }
    }

    private String generateDynamicMockResponse(List<Map<String, Object>> responses) {
        int totalRating = 0;
        int ratingCount = 0;
        int positiveCount = 0;
        int neutralCount = 0;
        int negativeCount = 0;
        
        java.util.List<String> posThemes = new java.util.ArrayList<>();
        java.util.List<String> negThemes = new java.util.ArrayList<>();
        
        for (Map<String, Object> response : responses) {
            if (response.containsKey("answers")) {
                List<Map<String, Object>> answers = (List<Map<String, Object>>) response.get("answers");
                for (Map<String, Object> ans : answers) {
                    if (ans.containsKey("ratingAnswer") && ans.get("ratingAnswer") != null) {
                        int rating = ((Number) ans.get("ratingAnswer")).intValue();
                        totalRating += rating;
                        ratingCount++;
                        if (rating >= 8) positiveCount++;
                        else if (rating >= 4) neutralCount++;
                        else negativeCount++;
                    }
                    if (ans.containsKey("textAnswer") && ans.get("textAnswer") != null) {
                        String text = ((String) ans.get("textAnswer")).toLowerCase();
                        if (text.contains("good") || text.contains("excellent") || text.contains("nice") || text.contains("great")) {
                            posThemes.add("Positive feedback on company processes");
                        }
                        if (text.contains("bad") || text.contains("poor") || text.contains("nothing") || text.contains("not")) {
                            negThemes.add("Specific areas requiring immediate improvement");
                        }
                    }
                }
            }
        }
        
        int overallScore = ratingCount > 0 ? (totalRating * 10) / ratingCount : 50;
        int posPerc = ratingCount > 0 ? (positiveCount * 100) / ratingCount : 33;
        int neuPerc = ratingCount > 0 ? (neutralCount * 100) / ratingCount : 33;
        int negPerc = ratingCount > 0 ? (negativeCount * 100) / ratingCount : 34;
        
        if (posThemes.isEmpty()) posThemes.add("Teamwork and general support");
        if (negThemes.isEmpty()) negThemes.add("Tooling and workflow inefficiencies");
        
        String summary = String.format("MOCKED DYNAMIC REPORT: Based on %d submitted responses, the overall sentiment score is %d/100. Positive sentiment is at %d%%, highlighting '%s', while negative sentiment is %d%%, pointing out '%s'.", 
                responses.size(), overallScore, posPerc, posThemes.get(0), negPerc, negThemes.get(0));
                
        try {
            Map<String, Object> mockResult = new java.util.HashMap<>();
            mockResult.put("overallScore", overallScore);
            mockResult.put("positivePercentage", posPerc);
            mockResult.put("neutralPercentage", neuPerc);
            mockResult.put("negativePercentage", negPerc);
            mockResult.put("executiveSummary", summary);
            mockResult.put("positiveThemes", posThemes.stream().distinct().collect(Collectors.toList()));
            mockResult.put("negativeThemes", negThemes.stream().distinct().collect(Collectors.toList()));
            mockResult.put("recommendations", java.util.Arrays.asList("Address the identified negative themes directly with the team.", "Continue promoting the positive behaviors observed."));
            mockResult.put("suggestedQuestions", new java.util.ArrayList<>());
            
            List<Map<String, Object>> mockQuestionAnalysis = new java.util.ArrayList<>();
            Map<String, Object> qa = new java.util.HashMap<>();
            qa.put("questionText", "Mocked Dynamic Question 1");
            qa.put("positivePercentage", posPerc);
            qa.put("neutralPercentage", neuPerc);
            qa.put("negativePercentage", negPerc);
            qa.put("summary", "Employees generally feel okay about this mocked question.");
            mockQuestionAnalysis.add(qa);
            
            mockResult.put("questionWiseAnalysis", mockQuestionAnalysis);
            
            return objectMapper.writeValueAsString(mockResult);
        } catch (Exception e) {
            return "{}";
        }
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SentimentAnalysisService.class);
    public SentimentAnalysisService(SurveyFeignClient surveyFeignClient, QuestionBankFeignClient questionBankFeignClient, GeminiClient geminiClient, PromptBuilder promptBuilder, GeminiResponseParser responseParser, SentimentReportRepository sentimentReportRepository, AiProcessingLogRepository aiProcessingLogRepository, KafkaTemplate<String, Object> kafkaTemplate, ReportingFeignClient reportingFeignClient, ObjectMapper objectMapper) {
        this.surveyFeignClient = surveyFeignClient;
        this.questionBankFeignClient = questionBankFeignClient;
        this.geminiClient = geminiClient;
        this.promptBuilder = promptBuilder;
        this.responseParser = responseParser;
        this.sentimentReportRepository = sentimentReportRepository;
        this.aiProcessingLogRepository = aiProcessingLogRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.reportingFeignClient = reportingFeignClient;
        this.objectMapper = objectMapper;
    }
}
