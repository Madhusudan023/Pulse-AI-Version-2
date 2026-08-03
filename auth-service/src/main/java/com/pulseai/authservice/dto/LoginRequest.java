package com.pulseai.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginRequest {
    @Schema(example = "global.hr@pulseai.com")
    private String email;
    @Schema(example = "password")
    private String password;
    public LoginRequest() {}
    public String getEmail() { return this.email; }
    public String getPassword() { return this.password; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
