package com.pulseai.surveyservice.entity;

import com.pulseai.surveyservice.enums.AssignmentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_assignments")
public class SurveyAssignment extends BaseEntity {

    private Long surveyId;
    
    private Long employeeId;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status = AssignmentStatus.PENDING;

    private LocalDateTime assignedAt;
    
    private LocalDateTime submittedAt;

    private boolean notificationSent = false;
    public Long getSurveyId() { return this.surveyId; }
    public Long getEmployeeId() { return this.employeeId; }
    public AssignmentStatus getStatus() { return this.status; }
    public LocalDateTime getAssignedAt() { return this.assignedAt; }
    public LocalDateTime getSubmittedAt() { return this.submittedAt; }
    public boolean isNotificationSent() { return this.notificationSent; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setStatus(AssignmentStatus status) { this.status = status; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public void setNotificationSent(boolean notificationSent) { this.notificationSent = notificationSent; }
}
