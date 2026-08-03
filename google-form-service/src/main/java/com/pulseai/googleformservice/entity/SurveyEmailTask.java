package com.pulseai.googleformservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "survey_email_tasks")
public class SurveyEmailTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long surveyId;
    
    private String email;
    
    private String formUrl;
    
    private String surveyTitle;
    
    private String status; // PENDING, SENT, FAILED
    
    private LocalDateTime createdAt;
    
    private LocalDateTime processedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSurveyId() { return surveyId; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFormUrl() { return formUrl; }
    public void setFormUrl(String formUrl) { this.formUrl = formUrl; }

    public String getSurveyTitle() { return surveyTitle; }
    public void setSurveyTitle(String surveyTitle) { this.surveyTitle = surveyTitle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
