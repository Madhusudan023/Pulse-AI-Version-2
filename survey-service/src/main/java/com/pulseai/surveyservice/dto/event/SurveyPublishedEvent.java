package com.pulseai.surveyservice.dto.event;

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
    public SurveyPublishedEvent(Long surveyId, String region, String title, LocalDateTime publishedAt, List<String> customEmails, List<Long> employeeIds, String experienceFilter) {
        this.surveyId = surveyId;
        this.region = region;
        this.title = title;
        this.publishedAt = publishedAt;
        this.customEmails = customEmails;
        this.employeeIds = employeeIds;
        this.experienceFilter = experienceFilter;
    }
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
    public static SurveyPublishedEventBuilder builder() { return new SurveyPublishedEventBuilder(); }
    public static class SurveyPublishedEventBuilder {
        private Long surveyId;
        private String region;
        private String title;
        private LocalDateTime publishedAt;
        private List<String> customEmails;
        private List<Long> employeeIds;
        private String experienceFilter;
        public SurveyPublishedEventBuilder surveyId(Long surveyId) { this.surveyId = surveyId; return this; }
        public SurveyPublishedEventBuilder region(String region) { this.region = region; return this; }
        public SurveyPublishedEventBuilder title(String title) { this.title = title; return this; }
        public SurveyPublishedEventBuilder publishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; return this; }
        public SurveyPublishedEventBuilder customEmails(List<String> customEmails) { this.customEmails = customEmails; return this; }
        public SurveyPublishedEventBuilder employeeIds(List<Long> employeeIds) { this.employeeIds = employeeIds; return this; }
        public SurveyPublishedEventBuilder experienceFilter(String experienceFilter) { this.experienceFilter = experienceFilter; return this; }
        public SurveyPublishedEvent build() { return new SurveyPublishedEvent(this.surveyId, this.region, this.title, this.publishedAt, this.customEmails, this.employeeIds, this.experienceFilter); }
    }
}
