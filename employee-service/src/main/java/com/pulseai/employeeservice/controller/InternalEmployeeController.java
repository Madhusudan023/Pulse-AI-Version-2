package com.pulseai.employeeservice.controller;

import com.pulseai.employeeservice.dto.ApiResponse;
import com.pulseai.employeeservice.dto.EmployeeInternalResponse;
import com.pulseai.employeeservice.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@RestController
@RequestMapping("/api/v1/internal/employees")
@Tag(name = "InternalEmployee APIs")
public class InternalEmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Endpoint for InternalEmployee")
    @GetMapping
    public ResponseEntity<List<EmployeeInternalResponse>> getEmployeesByRegion(@RequestParam String region) {
        return ResponseEntity.ok(employeeService.getInternalEmployeesByRegion(region));
    }

    @GetMapping("/new-joiners")
    public ResponseEntity<List<EmployeeInternalResponse>> getNewJoinersByRegion(@RequestParam String region) {
        return ResponseEntity.ok(employeeService.getNewJoinersByRegion(region));
    }

    @GetMapping("/tenured")
    public ResponseEntity<List<EmployeeInternalResponse>> getTenuredEmployeesByRegion(@RequestParam String region) {
        return ResponseEntity.ok(employeeService.getTenuredEmployeesByRegion(region));
    }

    @Operation(summary = "Endpoint for InternalEmployee")
    @GetMapping("/count")
    public ResponseEntity<java.util.Map<String, Long>> getEmployeeCountByRegion(@RequestParam String region) {
        return ResponseEntity.ok(java.util.Collections.singletonMap("count", employeeService.getEmployeeCountByRegion(region)));
    }

    @Operation(summary = "Endpoint for InternalEmployee")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeInternalResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeInternal(id));
    }

    @Operation(summary = "Endpoint for InternalEmployee")
    @GetMapping("/managers/{id}")
    public ResponseEntity<List<EmployeeInternalResponse>> getEmployeesByManager(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getInternalEmployeesByManager(id));
    }
    public InternalEmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
}
