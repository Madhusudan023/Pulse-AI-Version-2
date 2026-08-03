package com.pulseai.employeeservice.dto;

import com.pulseai.employeeservice.enums.Department;
import com.pulseai.employeeservice.enums.Region;
import com.pulseai.employeeservice.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeResponse {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String designation;
    private Department department;
    private String businessUnit;
    private Region region;
    private Long managerId;
    private Role role;
    private LocalDate joiningDate;
    private boolean active;
    private LocalDateTime createdAt;
    public EmployeeResponse() {}
    public Long getId() { return this.id; }
    public String getEmployeeCode() { return this.employeeCode; }
    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getEmail() { return this.email; }
    public String getDesignation() { return this.designation; }
    public Department getDepartment() { return this.department; }
    public String getBusinessUnit() { return this.businessUnit; }
    public Region getRegion() { return this.region; }
    public Long getManagerId() { return this.managerId; }
    public Role getRole() { return this.role; }
    public LocalDate getJoiningDate() { return this.joiningDate; }
    public boolean isActive() { return this.active; }
    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public void setId(Long id) { this.id = id; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setDesignation(String designation) { this.designation = designation; }
    public void setDepartment(Department department) { this.department = department; }
    public void setBusinessUnit(String businessUnit) { this.businessUnit = businessUnit; }
    public void setRegion(Region region) { this.region = region; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public void setRole(Role role) { this.role = role; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
