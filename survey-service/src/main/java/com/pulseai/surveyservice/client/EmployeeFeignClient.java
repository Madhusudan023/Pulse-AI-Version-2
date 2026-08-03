package com.pulseai.surveyservice.client;

import com.pulseai.surveyservice.dto.response.EmployeeInternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "employee-service", path = "/api/v1/internal/employees")
public interface EmployeeFeignClient {

    @GetMapping
    List<EmployeeInternalResponse> getEmployeesByRegion(@RequestParam("region") String region);
    
    @GetMapping("/new-joiners")
    List<EmployeeInternalResponse> getNewJoinersByRegion(@RequestParam("region") String region);
    
    @GetMapping("/tenured")
    List<EmployeeInternalResponse> getTenuredEmployeesByRegion(@RequestParam("region") String region);
}
