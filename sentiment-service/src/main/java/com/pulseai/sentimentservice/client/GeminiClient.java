package com.pulseai.sentimentservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import java.util.List;

@Component
public class GeminiClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "gemini-api", fallbackMethod = "generateContentFallback")
    @io.github.resilience4j.retry.annotation.Retry(name = "gemini-api")
    @io.github.resilience4j.bulkhead.annotation.Bulkhead(name = "gemini-api", type = io.github.resilience4j.bulkhead.annotation.Bulkhead.Type.SEMAPHORE)
    public Map<String, Object> generateContent(String promptText) {
        String[] geminiModels = {"gemini-3.5-flash", "gemini-2.5-flash-lite", "gemini-2.5-flash"};

        // Try Gemini models first
        for (String modelName : geminiModels) {
            String url = apiUrl.replaceAll("gemini-[0-9.]+-flash(-lite)?", modelName);
            log.info("Attempting to call Gemini API using model: {}", modelName);

            try {
                Map<String, Object> requestBody = Map.of(
                        "contents", new Object[]{
                                Map.of("parts", new Object[]{
                                        Map.of("text", promptText)
                                })
                        }
                );

                Map response = webClientBuilder.build()
                        .post()
                        .uri(url)
                        .header("x-goog-api-key", apiKey)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .timeout(java.time.Duration.ofSeconds(60))
                        .block();
                        
                if (response != null && response.containsKey("candidates")) {
                    List candidates = (List) response.get("candidates");
                    if (!candidates.isEmpty()) {
                        Map firstCandidate = (Map) candidates.get(0);
                        Map content = (Map) firstCandidate.get("content");
                        List parts = (List) content.get("parts");
                        if (!parts.isEmpty()) {
                            Map firstPart = (Map) parts.get(0);
                            String text = (String) firstPart.get("text");
                            
                            Map<String, Object> result = new java.util.HashMap<>();
                            result.put("text", text);
                            log.info("Successfully generated content with Gemini model {}", modelName);
                            return result;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Gemini Model {} failed or is unavailable. Trying next model if available...", modelName);
            }
        }
        
        log.warn("All Gemini models failed. Falling back to GroqCloud API...");
        
        // Try Groq models next (Llama, DeepSeek, Qwen, Gemma)
        String[] groqModels = {"llama-3.1-8b-instant", "llama-3.3-70b-versatile", "gemma2-9b-it", "qwen-2.5-32b", "deepseek-r1-distill-llama-70b"};
        
        for (String groqModelName : groqModels) {
            log.info("Attempting to call Groq API using model: {}", groqModelName);
            try {
                Map<String, Object> requestBody = Map.of(
                        "model", groqModelName,
                        "messages", new Object[]{
                                Map.of("role", "user", "content", promptText)
                        }
                );

                Map response = webClientBuilder.build()
                        .post()
                        .uri(groqApiUrl)
                        .header("Authorization", "Bearer " + groqApiKey)
                        .header("Content-Type", "application/json")
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .timeout(java.time.Duration.ofSeconds(60))
                        .block();
                        
                if (response != null && response.containsKey("choices")) {
                    List choices = (List) response.get("choices");
                    if (!choices.isEmpty()) {
                        Map firstChoice = (Map) choices.get(0);
                        Map message = (Map) firstChoice.get("message");
                        if (message != null && message.containsKey("content")) {
                            String text = (String) message.get("content");
                            
                            Map<String, Object> result = new java.util.HashMap<>();
                            result.put("text", text);
                            log.info("Successfully generated content with Groq model {}", groqModelName);
                            return result;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Groq Model {} failed or is unavailable. Trying next Groq model if available...", groqModelName);
            }
        }
        
        log.error("All AI models (Gemini & Groq) failed to generate content.");
        throw new RuntimeException("AI API call failed for all models");
    }

    public Map<String, Object> generateContentFallback(String promptText, Throwable t) {
        log.warn("Gemini REST API unavailable or timed out. Circuit breaker activated. Using mocked AI response.", t);
        
        Map<String, Object> result = new java.util.HashMap<>();
        String mockJson = "{" +
            "\"overallScore\": 85," +
            "\"positivePercentage\": 80," +
            "\"neutralPercentage\": 15," +
            "\"negativePercentage\": 5," +
            "\"executiveSummary\": \"MOCKED AI RESPONSE (No API Key): Overall sentiment in this region is highly positive. Employees feel well supported.\"," +
            "\"positiveThemes\": [\"Great teamwork\", \"Good benefits\"]," +
            "\"negativeThemes\": [\"Slow internal tools\"]," +
            "\"recommendations\": [\"Upgrade internal tooling performance\"]," +
            "\"suggestedQuestions\": []," +
            "\"questionWiseAnalysis\": [" +
            "  {" +
            "    \"questionText\": \"Static Mock Question\"," +
            "    \"positivePercentage\": 80," +
            "    \"neutralPercentage\": 15," +
            "    \"negativePercentage\": 5," +
            "    \"summary\": \"Overall positive static mocked response.\"" +
            "  }" +
            "]" +
        "}";
        
        result.put("text", mockJson);
        result.put("promptTokenCount", 0);
        result.put("candidatesTokenCount", 0);
        
        return result;
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GeminiClient.class);
    public GeminiClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
}
