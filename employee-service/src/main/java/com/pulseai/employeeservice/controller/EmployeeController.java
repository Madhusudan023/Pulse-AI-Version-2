package com.pulseai.employeeservice.controller;

import com.pulseai.employeeservice.constants.SecurityConstants;
import com.pulseai.employeeservice.dto.ApiResponse;
import com.pulseai.employeeservice.dto.CreateEmployeeRequest;
import com.pulseai.employeeservice.dto.EmployeeResponse;
import com.pulseai.employeeservice.dto.UpdateEmployeeRequest;
import com.pulseai.employeeservice.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee APIs")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Endpoint for Employee")
    @PostMapping
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR', 'VP')")
    public ResponseEntity<ApiResponse<Void>> createEmployee(@RequestBody CreateEmployeeRequest request, HttpServletRequest servletRequest) {
        String role = (String) servletRequest.getAttribute("role");
        String region = (String) servletRequest.getAttribute("region");
        
        if (SecurityConstants.ROLE_REGIONAL_HR.equals(role)) {
            // Regional HR can only create employees in their own region
            if (!request.getRegion().name().equals(region)) {
                return ResponseEntity.status(403).body(ApiResponse.<Void>builder()
                        .success(false).message("Cannot create employee outside your assigned region").build());
            }
        }

        employeeService.createEmployee(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Employee created successfully").build());
    }

    @Operation(summary = "Endpoint for Employee")
    @PostMapping("/regional-hrs")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'VP')")
    public ResponseEntity<ApiResponse<Void>> createRegionalHr(@RequestBody CreateEmployeeRequest request) {
        request.setRole(com.pulseai.employeeservice.enums.Role.REGIONAL_HR);
        employeeService.createEmployee(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Regional HR created successfully").build());
    }

    @Operation(summary = "Endpoint for Employee")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR', 'VP')")
    public ResponseEntity<ApiResponse<Void>> updateEmployee(@PathVariable Long id, @RequestBody UpdateEmployeeRequest request, HttpServletRequest servletRequest) {
        String role = (String) servletRequest.getAttribute("role");
        String region = (String) servletRequest.getAttribute("region");
        
        String enforceRegion = SecurityConstants.ROLE_REGIONAL_HR.equals(role) ? region : "GLOBAL";
        employeeService.updateEmployee(id, request, enforceRegion);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Employee updated successfully").build());
    }

    @Operation(summary = "Get Current User")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GLOBAL_HR', 'REGIONAL_HR', 'VP')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getMe(HttpServletRequest request) {
        Object empIdObj = request.getAttribute("employeeId");
        Long employeeId = empIdObj instanceof Number ? ((Number) empIdObj).longValue() : null;
        EmployeeResponse employee = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(ApiResponse.<EmployeeResponse>builder()
                .success(true).message("Employee retrieved").data(employee).build());
    }

    @Operation(summary = "Endpoint for Employee")
    @GetMapping
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR', 'VP')")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployees(HttpServletRequest servletRequest) {
        String role = (String) servletRequest.getAttribute("role");
        String region = (String) servletRequest.getAttribute("region");
        
        String queryRegion = SecurityConstants.ROLE_REGIONAL_HR.equals(role) ? region : "GLOBAL";
        
        List<EmployeeResponse> employees = employeeService.getEmployeesByRegion(queryRegion);
        return ResponseEntity.ok(ApiResponse.<List<EmployeeResponse>>builder()
                .success(true).message("Employees retrieved successfully").data(employees).build());
    }

    @Operation(summary = "Endpoint for Employee")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GLOBAL_HR', 'REGIONAL_HR', 'VP')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.<EmployeeResponse>builder()
                .success(true).message("Employee retrieved").data(employee).build());
    }
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}
