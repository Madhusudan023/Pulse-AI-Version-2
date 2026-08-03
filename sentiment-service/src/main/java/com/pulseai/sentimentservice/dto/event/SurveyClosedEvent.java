package com.pulseai.sentimentservice.dto.event;

import java.time.LocalDateTime;

public class SurveyClosedEvent {
    private Long surveyId;
    private String region;
    private String surveyType;
    private Integer month;
    private Integer year;
    @io.swagger.v3.oas.annotations.media.Schema(hidden = true)
    private LocalDateTime closedAt;
    public SurveyClosedEvent() {}
    public Long getSurveyId() { return this.surveyId; }
    public String getRegion() { return this.region; }
    public String getSurveyType() { return this.surveyType; }
    public Integer getMonth() { return this.month; }
    public Integer getYear() { return this.year; }
    public LocalDateTime getClosedAt() { return this.closedAt; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setRegion(String region) { this.region = region; }
    public void setSurveyType(String surveyType) { this.surveyType = surveyType; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
}
