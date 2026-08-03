package com.pulseai.surveyservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;


@Entity
@Table(name = "survey_responses")
public class SurveyResponse extends BaseEntity {

    private Long surveyId;
    
    private Long employeeId;
    
    private String employeeEmail;
    
    private LocalDateTime submittedAt;
    
    private String responseDuration; // e.g. "4 minutes"
    public Long getSurveyId() { return this.surveyId; }
    public Long getEmployeeId() { return this.employeeId; }
    public String getEmployeeEmail() { return this.employeeEmail; }
    public LocalDateTime getSubmittedAt() { return this.submittedAt; }
    public String getResponseDuration() { return this.responseDuration; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public void setResponseDuration(String responseDuration) { this.responseDuration = responseDuration; }
}
