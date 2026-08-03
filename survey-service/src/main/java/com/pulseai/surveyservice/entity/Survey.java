package com.pulseai.surveyservice.entity;

import com.pulseai.surveyservice.enums.SurveyStatus;
import com.pulseai.surveyservice.enums.SurveyType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "surveys")
public class Survey extends BaseEntity {

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String region;

    @Enumerated(EnumType.STRING)
    private SurveyType surveyType;

    private Integer month;
    private Integer year;

    @Enumerated(EnumType.STRING)
    private SurveyStatus status = SurveyStatus.DRAFT;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime publishedAt;
    private LocalDateTime closedAt;

    private Integer expectedParticipants = 0;
    private Integer completedParticipants = 0;

    private boolean aiProcessed = false;
    
    private boolean isAnonymous = true;
    
    @Column(name = "target_audience")
    private String targetAudience;
    
    public String getTitle() { return this.title; }
    public String getDescription() { return this.description; }
    public String getRegion() { return this.region; }
    public SurveyType getSurveyType() { return this.surveyType; }
    public Integer getMonth() { return this.month; }
    public Integer getYear() { return this.year; }
    public SurveyStatus getStatus() { return this.status; }
    public LocalDateTime getStartDate() { return this.startDate; }
    public LocalDateTime getEndDate() { return this.endDate; }
    public LocalDateTime getPublishedAt() { return this.publishedAt; }
    public LocalDateTime getClosedAt() { return this.closedAt; }
    public Integer getExpectedParticipants() { return this.expectedParticipants; }
    public Integer getCompletedParticipants() { return this.completedParticipants; }
    public boolean isAiProcessed() { return this.aiProcessed; }
    public boolean isAnonymous() { return this.isAnonymous; }
    public String getTargetAudience() { return this.targetAudience; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setRegion(String region) { this.region = region; }
    public void setSurveyType(SurveyType surveyType) { this.surveyType = surveyType; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setStatus(SurveyStatus status) { this.status = status; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
    public void setExpectedParticipants(Integer expectedParticipants) { this.expectedParticipants = expectedParticipants; }
    public void setCompletedParticipants(Integer completedParticipants) { this.completedParticipants = completedParticipants; }
    public void setAiProcessed(boolean aiProcessed) { this.aiProcessed = aiProcessed; }
    public void setAnonymous(boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }
}
