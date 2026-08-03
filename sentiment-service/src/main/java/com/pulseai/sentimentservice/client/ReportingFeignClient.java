package com.pulseai.sentimentservice.client;

import com.pulseai.sentimentservice.dto.event.AIReportGeneratedEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "reporting-service", path = "/api/internal/reporting")
public interface ReportingFeignClient {

    @PostMapping("/save-report")
    void saveReport(@RequestBody AIReportGeneratedEvent event);
}
