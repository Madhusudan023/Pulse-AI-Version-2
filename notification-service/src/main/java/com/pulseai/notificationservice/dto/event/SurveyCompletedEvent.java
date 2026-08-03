package com.pulseai.notificationservice.dto.event;

public class SurveyCompletedEvent {
    private Long surveyId;
    private Long employeeId;
    private String region;
    
    public SurveyCompletedEvent() {}
    
    public SurveyCompletedEvent(Long surveyId, Long employeeId, String region) {
        this.surveyId = surveyId;
        this.employeeId = employeeId;
        this.region = region;
    }
    
    public Long getSurveyId() { return surveyId; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
