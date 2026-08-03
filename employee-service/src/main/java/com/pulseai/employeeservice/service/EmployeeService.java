package com.pulseai.employeeservice.service;

import com.pulseai.employeeservice.client.AuthFeignClient;
import com.pulseai.employeeservice.dto.CreateCredentialRequest;
import com.pulseai.employeeservice.dto.CreateEmployeeRequest;
import com.pulseai.employeeservice.dto.EmployeeInternalResponse;
import com.pulseai.employeeservice.dto.EmployeeResponse;
import com.pulseai.employeeservice.dto.UpdateEmployeeRequest;
import com.pulseai.employeeservice.entity.Employee;
import com.pulseai.employeeservice.enums.Region;
import com.pulseai.employeeservice.exception.BusinessException;
import com.pulseai.employeeservice.exception.ResourceNotFoundException;
import com.pulseai.employeeservice.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuthFeignClient authFeignClient;

    @Transactional
    public void createEmployee(CreateEmployeeRequest request) {
        log.info("Creating employee: {}", request.getEmail());
        
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Employee with this email already exists");
        }

        Employee employee = new Employee();
        String generatedCode = "EMP" + String.format("%04d", employeeRepository.count() + 1);
        employee.setEmployeeCode(generatedCode);
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDesignation(request.getDesignation());
        employee.setDepartment(request.getDepartment());
        employee.setBusinessUnit(request.getBusinessUnit());
        employee.setRegion(request.getRegion());
        employee.setManagerId(request.getManagerId());
        employee.setRole(request.getRole());
        employee.setJoiningDate(request.getJoiningDate());
        
        Employee savedEmployee = employeeRepository.save(employee);
        
        try {
            log.info("Calling Auth Service to create credentials for: {}", request.getEmail());
            CreateCredentialRequest credentialRequest = CreateCredentialRequest.builder()
                    .employeeId(savedEmployee.getId())
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .role(request.getRole().name())
                    .region(request.getRegion().name())
                    .department(request.getDepartment() != null ? request.getDepartment().name() : null)
                    .build();
            
            authFeignClient.createCredential(credentialRequest);
            log.info("Credentials created successfully in Auth Service for: {}", request.getEmail());
        } catch (Exception e) {
            log.error("Failed to create credentials in Auth Service. Rolling back employee creation.", e);
            // This rollback ensures Employee is deleted if Auth creation fails (compensating transaction logic)
            throw new BusinessException("Failed to create employee credentials. Rolled back employee creation.");
        }
    }

    public void updateEmployee(Long id, UpdateEmployeeRequest request, String callerRegion) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (callerRegion != null && !callerRegion.equals("GLOBAL") && !employee.getRegion().name().equals(callerRegion)) {
            throw new BusinessException("You are not authorized to update employees outside your region");
        }

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setDesignation(request.getDesignation());
        employee.setDepartment(request.getDepartment());
        employee.setBusinessUnit(request.getBusinessUnit());
        employee.setRegion(request.getRegion());
        employee.setManagerId(request.getManagerId());
        employee.setRole(request.getRole());
        employee.setActive(request.isActive());
        
        employeeRepository.save(employee);
    }

    public List<EmployeeResponse> getEmployeesByRegion(String region) {
        List<Employee> employees;
        if ("GLOBAL".equals(region)) {
            employees = employeeRepository.findAll();
        } else {
            employees = employeeRepository.findByRegion(Region.valueOf(region));
        }
        return employees.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public EmployeeResponse getEmployeeById(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return mapToResponse(emp);
    }

    public EmployeeInternalResponse getEmployeeInternal(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        EmployeeInternalResponse res = new EmployeeInternalResponse();
        res.setEmployeeId(emp.getId());
        res.setEmail(emp.getEmail());
        res.setRegion(emp.getRegion());
        return res;
    }

    public List<EmployeeInternalResponse> getInternalEmployeesByRegion(String region) {
        List<Employee> employees = employeeRepository.findByRegion(Region.valueOf(region));
        return employees.stream().map(emp -> {
            EmployeeInternalResponse res = new EmployeeInternalResponse();
            res.setEmployeeId(emp.getId());
            res.setEmail(emp.getEmail());
            res.setRegion(emp.getRegion());
            return res;
        }).collect(Collectors.toList());
    }

    public List<EmployeeInternalResponse> getNewJoinersByRegion(String region) {
        java.time.LocalDate sixMonthsAgo = java.time.LocalDate.now().minusMonths(6);
        List<Employee> employees = employeeRepository.findByRegionAndJoiningDateGreaterThan(Region.valueOf(region), sixMonthsAgo);
        return employees.stream().map(emp -> {
            EmployeeInternalResponse res = new EmployeeInternalResponse();
            res.setEmployeeId(emp.getId());
            res.setEmail(emp.getEmail());
            res.setRegion(emp.getRegion());
            return res;
        }).collect(Collectors.toList());
    }

    public List<EmployeeInternalResponse> getTenuredEmployeesByRegion(String region) {
        java.time.LocalDate sixMonthsAgo = java.time.LocalDate.now().minusMonths(6);
        List<Employee> employees = employeeRepository.findByRegionAndJoiningDateLessThanEqual(Region.valueOf(region), sixMonthsAgo);
        return employees.stream().map(emp -> {
            EmployeeInternalResponse res = new EmployeeInternalResponse();
            res.setEmployeeId(emp.getId());
            res.setEmail(emp.getEmail());
            res.setRegion(emp.getRegion());
            return res;
        }).collect(Collectors.toList());
    }

    public List<EmployeeInternalResponse> getInternalEmployeesByManager(Long managerId) {
        List<Employee> employees = employeeRepository.findByManagerId(managerId);
        return employees.stream().map(emp -> {
            EmployeeInternalResponse res = new EmployeeInternalResponse();
            res.setEmployeeId(emp.getId());
            res.setEmail(emp.getEmail());
            res.setRegion(emp.getRegion());
            return res;
        }).collect(Collectors.toList());
    }

    public long getEmployeeCountByRegion(String region) {
        if ("GLOBAL".equals(region)) {
            return employeeRepository.count();
        }
        return employeeRepository.countByRegion(Region.valueOf(region));
    }

    private EmployeeResponse mapToResponse(Employee emp) {
        EmployeeResponse res = new EmployeeResponse();
        res.setId(emp.getId());
        res.setEmployeeCode(emp.getEmployeeCode());
        res.setFirstName(emp.getFirstName());
        res.setLastName(emp.getLastName());
        res.setEmail(emp.getEmail());
        res.setDesignation(emp.getDesignation());
        res.setDepartment(emp.getDepartment());
        res.setBusinessUnit(emp.getBusinessUnit());
        res.setRegion(emp.getRegion());
        res.setManagerId(emp.getManagerId());
        res.setRole(emp.getRole());
        res.setJoiningDate(emp.getJoiningDate());
        res.setActive(emp.isActive());
        res.setCreatedAt(emp.getCreatedAt());
        return res;
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmployeeService.class);
    public EmployeeService(EmployeeRepository employeeRepository, AuthFeignClient authFeignClient) {
        this.employeeRepository = employeeRepository;
        this.authFeignClient = authFeignClient;
    }
}
