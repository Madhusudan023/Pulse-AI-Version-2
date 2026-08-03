package com.pulseai.sentimentservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_processing_log")
public class AiProcessingLog extends BaseEntity {

    private Long surveyId;
    private String status; // e.g. STARTED, COMPLETED, FAILED
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    public Long getSurveyId() { return this.surveyId; }
    public LocalDateTime getStartedAt() { return this.startedAt; }
    public LocalDateTime getCompletedAt() { return this.completedAt; }
    public String getErrorMessage() { return this.errorMessage; }
    public String getStatus() { return this.status; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setStatus(String status) { this.status = status; }
}
