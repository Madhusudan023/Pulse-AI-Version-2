package com.pulseai.surveyservice.dto.event;

import com.pulseai.surveyservice.enums.SurveyType;

import java.time.LocalDateTime;

public class SurveyClosedEvent {
    private Long surveyId;
    private String region;
    private SurveyType surveyType;
    private Integer month;
    private Integer year;
    private LocalDateTime closedAt;
    public SurveyClosedEvent() {}
    public SurveyClosedEvent(Long surveyId, String region, SurveyType surveyType, Integer month, Integer year, LocalDateTime closedAt) {
        this.surveyId = surveyId;
        this.region = region;
        this.surveyType = surveyType;
        this.month = month;
        this.year = year;
        this.closedAt = closedAt;
    }
    public Long getSurveyId() { return this.surveyId; }
    public String getRegion() { return this.region; }
    public SurveyType getSurveyType() { return this.surveyType; }
    public Integer getMonth() { return this.month; }
    public Integer getYear() { return this.year; }
    public LocalDateTime getClosedAt() { return this.closedAt; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setRegion(String region) { this.region = region; }
    public void setSurveyType(SurveyType surveyType) { this.surveyType = surveyType; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
    public static  SurveyClosedEventBuilder builder() { return new SurveyClosedEventBuilder(); }
    public static class SurveyClosedEventBuilder {
        private Long surveyId;
        private String region;
        private SurveyType surveyType;
        private Integer month;
        private Integer year;
        private LocalDateTime closedAt;
        public SurveyClosedEventBuilder surveyId(Long surveyId) { this.surveyId = surveyId; return this; }
        public SurveyClosedEventBuilder region(String region) { this.region = region; return this; }
        public SurveyClosedEventBuilder surveyType(SurveyType surveyType) { this.surveyType = surveyType; return this; }
        public SurveyClosedEventBuilder month(Integer month) { this.month = month; return this; }
        public SurveyClosedEventBuilder year(Integer year) { this.year = year; return this; }
        public SurveyClosedEventBuilder closedAt(LocalDateTime closedAt) { this.closedAt = closedAt; return this; }
        public SurveyClosedEvent build() { return new SurveyClosedEvent(this.surveyId, this.region, this.surveyType, this.month, this.year, this.closedAt); }
    }
}
