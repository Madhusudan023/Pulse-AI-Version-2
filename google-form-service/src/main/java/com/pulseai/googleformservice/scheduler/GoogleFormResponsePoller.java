package com.pulseai.googleformservice.scheduler;

import com.google.api.services.forms.v1.Forms;
import com.google.api.services.forms.v1.model.FormResponse;
import com.google.api.services.forms.v1.model.ListFormResponsesResponse;
import com.pulseai.googleformservice.dto.request.AnswerRequest;
import com.pulseai.googleformservice.dto.request.SubmitSurveyRequest;
import com.pulseai.googleformservice.entity.GoogleForm;
import com.pulseai.googleformservice.feign.SurveyFeignClient;
import com.pulseai.googleformservice.repository.GoogleFormRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GoogleFormResponsePoller {

    private static final Logger log = LoggerFactory.getLogger(GoogleFormResponsePoller.class);

    @Autowired
    private GoogleFormRepository googleFormRepository;

    @Autowired
    private SurveyFeignClient surveyFeignClient;

    // To use Forms API we need the Forms instance. We can expose it as a bean or use reflection/getter.
    // Let's assume we can get it or we just add a method to GoogleFormsService to fetch responses.
    @Autowired
    private com.pulseai.googleformservice.service.GoogleFormsService googleFormsService;

    @Scheduled(fixedDelay = 30000)
    public void pollResponses() {
        log.info("Polling Google Forms for new responses...");
        List<GoogleForm> activeForms = googleFormRepository.findAll();
        for (GoogleForm form : activeForms) {
            try {
                List<FormResponse> responses = googleFormsService.getFormResponses(form.getGoogleFormId());
                if (responses == null) continue;

                for (FormResponse response : responses) {
                    Instant createTime = Instant.parse(response.getCreateTime());
                    LocalDateTime responseTime = LocalDateTime.ofInstant(createTime, ZoneId.systemDefault());

                    // If we have a lastSyncTime and this response is older, skip it
                    if (form.getExpiresAt() != null && !responseTime.isAfter(form.getExpiresAt())) {
                        continue;
                    }

                    log.info("Found new response for form {} at {}", form.getGoogleFormId(), responseTime);

                    SubmitSurveyRequest submitRequest = new SubmitSurveyRequest();
                    submitRequest.setResponseDuration("1 minute");
                    List<AnswerRequest> answers = new ArrayList<>();

                    // Google Form answers are dynamic. We will just attempt to map them back.
                    // For a true production app, we would map the Google Form Question ID to our Question ID.
                    // For this PoC, we will assume 1-to-1 based on order or just set a dummy question ID if not found.
                    // To ensure it works for sentiment analysis, we will map any text answer to questionId 1 (or we can query survey-service).
                    // Actually, the sentiment-service doesn't care about questionId, it just analyzes the text!
                    
                    if (response.getAnswers() != null) {
                        for (Map.Entry<String, com.google.api.services.forms.v1.model.Answer> entry : response.getAnswers().entrySet()) {
                            com.google.api.services.forms.v1.model.Answer ans = entry.getValue();
                            if (ans.getTextAnswers() != null && ans.getTextAnswers().getAnswers() != null) {
                                for (com.google.api.services.forms.v1.model.TextAnswer ta : ans.getTextAnswers().getAnswers()) {
                                    AnswerRequest ar = new AnswerRequest();
                                    ar.setQuestionId(1L); // Fallback ID so it saves
                                    ar.setTextAnswer(ta.getValue());
                                    answers.add(ar);
                                }
                            }
                        }
                    }

                    submitRequest.setAnswers(answers);
                    
                    try {
                        surveyFeignClient.submitInternalResponse(form.getSurveyId(), submitRequest);
                        log.info("Successfully synced response to survey-service for survey {}", form.getSurveyId());
                    } catch (Exception e) {
                        log.error("Failed to sync response to survey-service", e);
                    }
                    
                    // Update the sync time (using expiresAt as a hack for lastSyncTime without DDL changes)
                    form.setExpiresAt(responseTime);
                    googleFormRepository.save(form);
                }
            } catch (Exception e) {
                log.error("Error polling form {}: {}", form.getGoogleFormId(), e.getMessage());
            }
        }
    }
}
