package com.pulseai.notificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "employee-service", path = "/api/v1/internal")
public interface EmployeeFeignClient {

    @GetMapping("/employees")
    List<EmployeeInternalDTO> getEmployeesByRegion(@org.springframework.web.bind.annotation.RequestParam("region") String region);

    @GetMapping("/employees/hr")
    List<EmployeeInternalDTO> getRegionalHr(@org.springframework.web.bind.annotation.RequestParam("region") String region);

    @GetMapping("/employees/global-hr")
    List<EmployeeInternalDTO> getGlobalHr();

    @GetMapping("/employees/vp")
    List<EmployeeInternalDTO> getVp();
    
    public static class EmployeeInternalDTO {
        private Long employeeId;
        private String email;
        private String firstName;
        private String lastName;
        private String region;
        private String role;
        
        public EmployeeInternalDTO() {}
        public Long getEmployeeId() { return this.employeeId; }
        public String getEmail() { return this.email; }
        public String getFirstName() { return this.firstName; }
        public String getLastName() { return this.lastName; }
        public String getRegion() { return this.region; }
        public String getRole() { return this.role; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
        public void setEmail(String email) { this.email = email; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public void setRegion(String region) { this.region = region; }
        public void setRole(String role) { this.role = role; }
    }
}
