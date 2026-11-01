package com.trainingmug.asset.client;

import com.trainingmug.asset.dto.EmployeeServiceEmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/** Reads employee details needed to validate asset assignment. */
@FeignClient(name = "employee-service", fallbackFactory = EmployeeClientFallbackFactory.class)
public interface EmployeeClient {
    @GetMapping("/api/employees/{id}")
    EmployeeServiceEmployeeResponse get(@PathVariable("id") UUID id);
}
