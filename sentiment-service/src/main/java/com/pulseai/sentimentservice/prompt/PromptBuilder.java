package com.pulseai.sentimentservice.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PromptBuilder {

    private final ObjectMapper objectMapper;

    public String buildPrompt(Map<String, Object> surveyContext, List<Map<String, Object>> responsesAndAnswers) {
        try {
            String contextJson = objectMapper.writeValueAsString(surveyContext);
            String answersJson = objectMapper.writeValueAsString(responsesAndAnswers);
            
            return "You are an expert HR Data Scientist and Organizational Psychologist.\n" +
                   "Analyze the following employee survey data.\n" +
                   "Survey Context:\n" + contextJson + "\n\n" +
                   "Raw Responses:\n" + answersJson + "\n\n" +
                   "INSTRUCTIONS:\n" +
                   "1. Calculate an 'overallScore' (0-100) based on the general sentiment.\n" +
                   "2. Estimate the percentage of positive, neutral, and negative sentiment.\n" +
                   "3. Write a concise 'executiveSummary' of the findings.\n" +
                   "4. Extract a list of 'positiveThemes' and 'negativeThemes'.\n" +
                   "5. Provide actionable 'recommendations' for HR.\n" +
                   "6. Generate 3-5 'suggestedQuestions' for the NEXT monthly survey to dig deeper into the negative themes. For each question, provide 'questionText', 'category' (e.g. WORK_LIFE_BALANCE, LEADERSHIP, WORKLOAD), and 'questionType' (LIKERT_SCALE, MCQ, TEXT).\n" +
                   "7. Generate 'questionWiseAnalysis', calculating the positivePercentage, neutralPercentage, and negativePercentage for EACH distinct question in the raw responses, along with a 1-sentence 'summary'.\n\n" +
                   "Return ONLY a valid JSON object matching the following structure. Do not include markdown blocks (```json) or any other text.\n" +
                   "{\n" +
                   "  \"overallScore\": 85,\n" +
                   "  \"positivePercentage\": 60,\n" +
                   "  \"neutralPercentage\": 25,\n" +
                   "  \"negativePercentage\": 15,\n" +
                   "  \"executiveSummary\": \"...\",\n" +
                   "  \"positiveThemes\": [\"...\"],\n" +
                   "  \"negativeThemes\": [\"...\"],\n" +
                   "  \"recommendations\": [\"...\"],\n" +
                   "  \"suggestedQuestions\": [\n" +
                   "    {\n" +
                   "      \"questionText\": \"...\",\n" +
                   "      \"category\": \"...\",\n" +
                   "      \"questionType\": \"...\"\n" +
                   "    }\n" +
                   "  ],\n" +
                   "  \"questionWiseAnalysis\": [\n" +
                   "    {\n" +
                   "      \"questionText\": \"...\",\n" +
                   "      \"positivePercentage\": 70,\n" +
                   "      \"neutralPercentage\": 20,\n" +
                   "      \"negativePercentage\": 10,\n" +
                   "      \"summary\": \"...\"\n" +
                   "    }\n" +
                   "  ]\n" +
                   "}";
        } catch (Exception e) {
            throw new RuntimeException("Failed to build prompt", e);
        }
    }
    public PromptBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
