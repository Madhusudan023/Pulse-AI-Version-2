package com.pulseai.sentimentservice.dto.event;


import java.time.LocalDateTime;

public class AIReportGeneratedEvent {
    private Long reportId;
    private Long surveyId;
    private String region;
    private Integer month;
    private Integer year;
    private LocalDateTime generatedAt;
    public AIReportGeneratedEvent() {}
    public AIReportGeneratedEvent(Long reportId, Long surveyId, String region, Integer month, Integer year, LocalDateTime generatedAt) {
        this.reportId = reportId;
        this.surveyId = surveyId;
        this.region = region;
        this.month = month;
        this.year = year;
        this.generatedAt = generatedAt;
    }
    public Long getReportId() { return this.reportId; }
    public Long getSurveyId() { return this.surveyId; }
    public String getRegion() { return this.region; }
    public Integer getMonth() { return this.month; }
    public Integer getYear() { return this.year; }
    public LocalDateTime getGeneratedAt() { return this.generatedAt; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setRegion(String region) { this.region = region; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public static  AIReportGeneratedEventBuilder builder() { return new AIReportGeneratedEventBuilder(); }
    public static class AIReportGeneratedEventBuilder {
        private Long reportId;
        private Long surveyId;
        private String region;
        private Integer month;
        private Integer year;
        private LocalDateTime generatedAt;
        public AIReportGeneratedEventBuilder reportId(Long reportId) { this.reportId = reportId; return this; }
        public AIReportGeneratedEventBuilder surveyId(Long surveyId) { this.surveyId = surveyId; return this; }
        public AIReportGeneratedEventBuilder region(String region) { this.region = region; return this; }
        public AIReportGeneratedEventBuilder month(Integer month) { this.month = month; return this; }
        public AIReportGeneratedEventBuilder year(Integer year) { this.year = year; return this; }
        public AIReportGeneratedEventBuilder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }
        public AIReportGeneratedEvent build() { return new AIReportGeneratedEvent(this.reportId, this.surveyId, this.region, this.month, this.year, this.generatedAt); }
    }
}
