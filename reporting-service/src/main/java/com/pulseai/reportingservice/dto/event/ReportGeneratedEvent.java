package com.pulseai.reportingservice.dto.event;


import java.time.LocalDateTime;

public class ReportGeneratedEvent {
    private Long reportId;
    private Long surveyId;
    private String region;
    private Integer month;
    private Integer year;
    private LocalDateTime generatedAt;
    public ReportGeneratedEvent() {}
    public ReportGeneratedEvent(Long reportId, Long surveyId, String region, Integer month, Integer year, LocalDateTime generatedAt) {
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
    public static  ReportGeneratedEventBuilder builder() { return new ReportGeneratedEventBuilder(); }
    public static class ReportGeneratedEventBuilder {
        private Long reportId;
        private Long surveyId;
        private String region;
        private Integer month;
        private Integer year;
        private LocalDateTime generatedAt;
        public ReportGeneratedEventBuilder reportId(Long reportId) { this.reportId = reportId; return this; }
        public ReportGeneratedEventBuilder surveyId(Long surveyId) { this.surveyId = surveyId; return this; }
        public ReportGeneratedEventBuilder region(String region) { this.region = region; return this; }
        public ReportGeneratedEventBuilder month(Integer month) { this.month = month; return this; }
        public ReportGeneratedEventBuilder year(Integer year) { this.year = year; return this; }
        public ReportGeneratedEventBuilder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }
        public ReportGeneratedEvent build() { return new ReportGeneratedEvent(this.reportId, this.surveyId, this.region, this.month, this.year, this.generatedAt); }
    }
}
