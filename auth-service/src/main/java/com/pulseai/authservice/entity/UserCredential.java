package com.pulseai.authservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_credentials")
public class UserCredential extends BaseEntity {

    private Long employeeId;
    
    private String email;
    
    private String password;
    
    private String role;
    
    private String region;
    
    private String department;
    
    private boolean accountLocked = false;
    
    private LocalDateTime lastLogin;
    public Long getEmployeeId() { return this.employeeId; }
    public String getEmail() { return this.email; }
    public String getPassword() { return this.password; }
    public String getRole() { return this.role; }
    public String getRegion() { return this.region; }
    public String getDepartment() { return this.department; }
    public boolean isAccountLocked() { return this.accountLocked; }
    public LocalDateTime getLastLogin() { return this.lastLogin; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setRegion(String region) { this.region = region; }
    public void setDepartment(String department) { this.department = department; }
    public void setAccountLocked(boolean accountLocked) { this.accountLocked = accountLocked; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
}
