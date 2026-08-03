package com.pulseai.notificationservice.dto.event;

import java.time.LocalDateTime;

public class ReportGeneratedEvent {
    private Long reportId;
    private Long surveyId;
    private String region;
    private Integer month;
    private Integer year;
    private LocalDateTime generatedAt;
    public ReportGeneratedEvent() {}
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
}
