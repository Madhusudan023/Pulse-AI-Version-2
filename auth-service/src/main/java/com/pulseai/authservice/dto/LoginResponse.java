package com.pulseai.authservice.dto;


public class LoginResponse {
    private String token;
    private Long employeeId;
    private String email;
    private String role;
    private String region;
    public LoginResponse() {}
    public LoginResponse(String token, Long employeeId, String email, String role, String region) {
        this.token = token;
        this.employeeId = employeeId;
        this.email = email;
        this.role = role;
        this.region = region;
    }
    public String getToken() { return this.token; }
    public Long getEmployeeId() { return this.employeeId; }
    public String getEmail() { return this.email; }
    public String getRole() { return this.role; }
    public String getRegion() { return this.region; }
    public void setToken(String token) { this.token = token; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setRegion(String region) { this.region = region; }
    public static LoginResponseBuilder builder() { return new LoginResponseBuilder(); }
    public static class LoginResponseBuilder {
        private String token;
        private Long employeeId;
        private String email;
        private String role;
        private String region;
        public LoginResponseBuilder token(String token) { this.token = token; return this; }
        public LoginResponseBuilder employeeId(Long employeeId) { this.employeeId = employeeId; return this; }
        public LoginResponseBuilder email(String email) { this.email = email; return this; }
        public LoginResponseBuilder role(String role) { this.role = role; return this; }
        public LoginResponseBuilder region(String region) { this.region = region; return this; }
        public LoginResponse build() { return new LoginResponse(this.token, this.employeeId, this.email, this.role, this.region); }
    }
}
