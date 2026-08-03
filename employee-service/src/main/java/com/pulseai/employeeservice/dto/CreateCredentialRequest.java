package com.pulseai.employeeservice.dto;


public class CreateCredentialRequest {
    private Long employeeId;
    private String email;
    private String password;
    private String role;
    private String region;
    private String department;
    public CreateCredentialRequest() {}
    public CreateCredentialRequest(Long employeeId, String email, String password, String role, String region, String department) {
        this.employeeId = employeeId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.region = region;
        this.department = department;
    }
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
    public static  CreateCredentialRequestBuilder builder() { return new CreateCredentialRequestBuilder(); }
    public static class CreateCredentialRequestBuilder {
        private Long employeeId;
        private String email;
        private String password;
        private String role;
        private String region;
        private String department;
        public CreateCredentialRequestBuilder employeeId(Long employeeId) { this.employeeId = employeeId; return this; }
        public CreateCredentialRequestBuilder email(String email) { this.email = email; return this; }
        public CreateCredentialRequestBuilder password(String password) { this.password = password; return this; }
        public CreateCredentialRequestBuilder role(String role) { this.role = role; return this; }
        public CreateCredentialRequestBuilder region(String region) { this.region = region; return this; }
        public CreateCredentialRequestBuilder department(String department) { this.department = department; return this; }
        public CreateCredentialRequest build() { return new CreateCredentialRequest(this.employeeId, this.email, this.password, this.role, this.region, this.department); }
    }
}
