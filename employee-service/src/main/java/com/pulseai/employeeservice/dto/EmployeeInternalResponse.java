package com.pulseai.employeeservice.dto;

import com.pulseai.employeeservice.enums.Region;

public class EmployeeInternalResponse {
    private Long employeeId;
    private String email;
    private Region region;
    public EmployeeInternalResponse() {}
    public Long getEmployeeId() { return this.employeeId; }
    public String getEmail() { return this.email; }
    public Region getRegion() { return this.region; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setEmail(String email) { this.email = email; }
    public void setRegion(Region region) { this.region = region; }
}
