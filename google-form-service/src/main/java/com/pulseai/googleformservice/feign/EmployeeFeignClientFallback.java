package com.pulseai.googleformservice.feign;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class EmployeeFeignClientFallback implements EmployeeFeignClient {
    @Override
    public List<Object> getEmployeesByRegion(String region) {
        return Collections.emptyList();
    }
}
