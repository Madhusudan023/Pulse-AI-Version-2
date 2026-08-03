package com.pulseai.authservice.dto;


public class CreateCredentialRequest {
    private Long employeeId;
    private String email;
    private String password;
    private String role;
    private String region;
    private String department;
    public CreateCredentialRequest() {}
    public Long getEmployeeId() { return this.employeeId; }
    public String getEmail() { return this.email; }
    public String getPassword() { return this.password; }
    public String getRole() { return this.role; }
    public String getRegion() { return this.region; }
    public String getDepartment() { return this.department; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setRegion(String region) { this.region = region; }
    public void setDepartment(String department) { this.department = department; }
}
