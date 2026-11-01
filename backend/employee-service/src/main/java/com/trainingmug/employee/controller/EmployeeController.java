package com.trainingmug.employee.controller;

import com.trainingmug.employee.dto.CreateEmployeeRequest;
import com.trainingmug.employee.dto.EmployeeResponse;
import com.trainingmug.employee.dto.EmploymentStatusRequest;
import com.trainingmug.employee.dto.SyncRequest;
import com.trainingmug.employee.dto.UpdateEmployeeRequest;
import com.trainingmug.employee.enums.IntegrationSystem;
import com.trainingmug.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** REST endpoints for employee profiles, onboarding, and sync state. */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Create an employee and initialize Slack/GitHub sync states")
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody CreateEmployeeRequest request
    ) {
        EmployeeResponse created = employeeService.createEmployee(request);
        URI location = URI.create("/api/employees/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Search employee profiles")
    @GetMapping
    public List<EmployeeResponse> list(
            @RequestParam(value = "search", required = false) String search
    ) {
        return employeeService.listEmployees(search);
    }

    @Operation(summary = "Get an employee profile and system sync state")
    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable("id") UUID id) {
        return employeeService.getEmployee(id);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateEmployeeRequest request
    ) {
        return employeeService.updateEmployee(id, request);
    }

    @PostMapping("/{id}/onboarding/advance")
    public EmployeeResponse advance(@PathVariable("id") UUID id) {
        return employeeService.advanceOnboardingStage(id);
    }

    @PatchMapping("/{id}/employment-status")
    public EmployeeResponse employment(
            @PathVariable("id") UUID id,
            @Valid @RequestBody EmploymentStatusRequest request
    ) {
        return employeeService.updateEmploymentStatus(id, request.status());
    }

    @PatchMapping("/{id}/system-sync/{system}")
    public EmployeeResponse sync(
            @PathVariable("id") UUID id,
            @PathVariable("system") String system,
            @Valid @RequestBody SyncRequest request
    ) {
        IntegrationSystem integrationSystem = IntegrationSystem.valueOf(
                system.toUpperCase(Locale.ROOT));
        return employeeService.updateSystemSync(
                id, integrationSystem, request.status(), request.failureMessage());
    }
}
