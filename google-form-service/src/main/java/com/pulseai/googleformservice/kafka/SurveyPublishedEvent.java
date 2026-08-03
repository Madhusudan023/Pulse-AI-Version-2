package com.pulseai.googleformservice.kafka;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyPublishedEvent {
    private Long surveyId;
    private String region;
    private String title;
    private String targetAudience;
    private LocalDateTime publishedAt;
    private java.time.LocalDate endDate;
    private List<String> customEmails;
    private List<Long> employeeIds;
    private String experienceFilter;

    public SurveyPublishedEvent() {}

    public Long getSurveyId() { return surveyId; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public java.time.LocalDate getEndDate() { return endDate; }
    public void setEndDate(java.time.LocalDate endDate) { this.endDate = endDate; }
    
    public List<String> getCustomEmails() { return customEmails; }
    public void setCustomEmails(List<String> customEmails) { this.customEmails = customEmails; }
    
    public List<Long> getEmployeeIds() { return employeeIds; }
    public void setEmployeeIds(List<Long> employeeIds) { this.employeeIds = employeeIds; }

    public String getExperienceFilter() { return experienceFilter; }
    public void setExperienceFilter(String experienceFilter) { this.experienceFilter = experienceFilter; }
}
