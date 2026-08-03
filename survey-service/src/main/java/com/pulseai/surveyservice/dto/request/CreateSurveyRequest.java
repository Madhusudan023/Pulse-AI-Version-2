package com.pulseai.surveyservice.dto.request;

import com.pulseai.surveyservice.enums.SurveyType;

import java.time.LocalDateTime;

public class CreateSurveyRequest {
    private String title;
    private String description;
    private String region;
    private SurveyType surveyType;
    private Integer month;
    private Integer year;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String targetAudience;
    private boolean isAnonymous;
    
    public CreateSurveyRequest() {}
    public String getTitle() { return this.title; }
    public String getDescription() { return this.description; }
    public String getRegion() { return this.region; }
    public SurveyType getSurveyType() { return this.surveyType; }
    public Integer getMonth() { return this.month; }
    public Integer getYear() { return this.year; }
    public LocalDateTime getStartDate() { return this.startDate; }
    public LocalDateTime getEndDate() { return this.endDate; }
    public String getTargetAudience() { return this.targetAudience; }
    public boolean isAnonymous() { return this.isAnonymous; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setRegion(String region) { this.region = region; }
    public void setSurveyType(SurveyType surveyType) { this.surveyType = surveyType; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }
    public void setAnonymous(boolean isAnonymous) { this.isAnonymous = isAnonymous; }
}
