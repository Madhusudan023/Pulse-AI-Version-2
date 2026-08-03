package com.pulseai.employeeservice.entity;

import com.pulseai.employeeservice.enums.Department;
import com.pulseai.employeeservice.enums.Region;
import com.pulseai.employeeservice.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String employeeCode;
    
    private String firstName;
    
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String designation;
    
    @Enumerated(EnumType.STRING)
    private Department department;
    
    private String businessUnit;
    
    @Enumerated(EnumType.STRING)
    private Region region;
    
    private Long managerId;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private LocalDate joiningDate;
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
}
