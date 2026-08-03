package com.pulseai.notificationservice.dto.event;

import java.util.List;

public class SurveyReminderEvent {
    private Long surveyId;
    private String region;
    private String title;
    private List<Long> employeeIds;
    public SurveyReminderEvent() {}
    public Long getSurveyId() { return this.surveyId; }
    public String getRegion() { return this.region; }
    public String getTitle() { return this.title; }
    public List<Long> getEmployeeIds() { return this.employeeIds; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setRegion(String region) { this.region = region; }
    public void setTitle(String title) { this.title = title; }
    public void setEmployeeIds(List<Long> employeeIds) { this.employeeIds = employeeIds; }
}
