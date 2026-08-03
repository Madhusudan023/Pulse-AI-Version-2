package com.pulseai.googleformservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "employee-service", fallback = EmployeeFeignClientFallback.class)
public interface EmployeeFeignClient {
    @GetMapping("/api/v1/internal/employees")
    List<Object> getEmployeesByRegion(@RequestParam("region") String region);
}
