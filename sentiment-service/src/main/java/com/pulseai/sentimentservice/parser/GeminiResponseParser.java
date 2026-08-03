package com.pulseai.sentimentservice.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseai.sentimentservice.dto.response.SentimentAnalysisResult;
import org.springframework.stereotype.Component;

@Component
public class GeminiResponseParser {

    private final ObjectMapper objectMapper;

    public SentimentAnalysisResult parse(String rawResponse) {
        try {
            // Gemini sometimes wraps the output in ```json ... ``` markdown blocks even when instructed not to.
            // Clean it up if necessary.
            String cleanJson = rawResponse.trim();
            if (cleanJson.startsWith("```json")) {
                cleanJson = cleanJson.substring(7);
            }
            if (cleanJson.startsWith("```")) {
                cleanJson = cleanJson.substring(3);
            }
            if (cleanJson.endsWith("```")) {
                cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
            }
            
            return objectMapper.readValue(cleanJson.trim(), SentimentAnalysisResult.class);
        } catch (Exception e) {
            log.error("Failed to parse Gemini JSON response: {}", rawResponse, e);
            throw new RuntimeException("Failed to parse AI response into JSON", e);
        }
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GeminiResponseParser.class);
    public GeminiResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
