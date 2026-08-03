package com.pulseai.authservice.dto;


public class ChangePasswordRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
    public ChangePasswordRequest() {}
    public String getEmail() { return this.email; }
    public String getOldPassword() { return this.oldPassword; }
    public String getNewPassword() { return this.newPassword; }
    public void setEmail(String email) { this.email = email; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
