package com.pulseai.notificationservice.dto.event;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyPublishedEvent {
    private Long surveyId;
    private String region;
    private String title;
    private LocalDateTime publishedAt;
    private List<String> customEmails;
    private List<Long> employeeIds;
    private String experienceFilter;
    public SurveyPublishedEvent() {}
    public Long getSurveyId() { return this.surveyId; }
    public String getRegion() { return this.region; }
    public String getTitle() { return this.title; }
    public LocalDateTime getPublishedAt() { return this.publishedAt; }
    public List<String> getCustomEmails() { return this.customEmails; }
    public List<Long> getEmployeeIds() { return this.employeeIds; }
    public String getExperienceFilter() { return this.experienceFilter; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setRegion(String region) { this.region = region; }
    public void setTitle(String title) { this.title = title; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public void setCustomEmails(List<String> customEmails) { this.customEmails = customEmails; }
    public void setEmployeeIds(List<Long> employeeIds) { this.employeeIds = employeeIds; }
    public void setExperienceFilter(String experienceFilter) { this.experienceFilter = experienceFilter; }
}
