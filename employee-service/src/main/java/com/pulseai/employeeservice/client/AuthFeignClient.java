package com.pulseai.employeeservice.client;

import com.pulseai.employeeservice.config.FeignConfig;
import com.pulseai.employeeservice.dto.ApiResponse;
import com.pulseai.employeeservice.dto.CreateCredentialRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface AuthFeignClient {

    @PostMapping("/api/v1/auth/create")
    ApiResponse<Void> createCredential(@RequestBody CreateCredentialRequest request);
}
