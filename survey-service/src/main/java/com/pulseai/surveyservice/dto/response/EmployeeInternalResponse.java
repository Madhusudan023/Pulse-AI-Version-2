package com.pulseai.surveyservice.dto.response;


public class EmployeeInternalResponse {
    private Long employeeId;
    private String email;
    private String region;
    public EmployeeInternalResponse() {}
    public Long getEmployeeId() { return this.employeeId; }
    public String getEmail() { return this.email; }
    public String getRegion() { return this.region; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setEmail(String email) { this.email = email; }
    public void setRegion(String region) { this.region = region; }
}
