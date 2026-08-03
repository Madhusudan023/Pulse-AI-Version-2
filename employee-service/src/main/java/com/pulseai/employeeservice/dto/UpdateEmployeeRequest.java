package com.pulseai.employeeservice.dto;

import com.pulseai.employeeservice.enums.Department;
import com.pulseai.employeeservice.enums.Region;
import com.pulseai.employeeservice.enums.Role;

public class UpdateEmployeeRequest {
    private String firstName;
    private String lastName;
    private String designation;
    private Department department;
    private String businessUnit;
    private Region region;
    private Long managerId;
    private Role role;
    private boolean active;
    public UpdateEmployeeRequest() {}
    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getDesignation() { return this.designation; }
    public Department getDepartment() { return this.department; }
    public String getBusinessUnit() { return this.businessUnit; }
    public Region getRegion() { return this.region; }
    public Long getManagerId() { return this.managerId; }
    public Role getRole() { return this.role; }
    public boolean isActive() { return this.active; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDesignation(String designation) { this.designation = designation; }
    public void setDepartment(Department department) { this.department = department; }
    public void setBusinessUnit(String businessUnit) { this.businessUnit = businessUnit; }
    public void setRegion(Region region) { this.region = region; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public void setRole(Role role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
}
