package com.trainingmug.asset.client;

import com.trainingmug.asset.exception.EmployeeNotFoundException;
import com.trainingmug.asset.exception.EmployeeServiceUnavailableException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/** Translates employee-service failures into stable asset-service exceptions. */
@Component
public class EmployeeClientFallbackFactory implements FallbackFactory<EmployeeClient> {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmployeeClientFallbackFactory.class);

    @Override
    public EmployeeClient create(Throwable cause) {
        return employeeId -> {
            if (cause instanceof FeignException.NotFound) {
                throw new EmployeeNotFoundException();
            }
            LOGGER.warn("Employee lookup failed while validating asset assignment", cause);
            throw new EmployeeServiceUnavailableException();
        };
    }
}
