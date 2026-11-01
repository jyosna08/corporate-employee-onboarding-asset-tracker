package com.trainingmug.employee.service;

import com.trainingmug.employee.dto.CreateEmployeeRequest;
import com.trainingmug.employee.dto.EmployeeResponse;
import com.trainingmug.employee.dto.UpdateEmployeeRequest;
import com.trainingmug.employee.entity.EmployeeEntity;
import com.trainingmug.employee.entity.EmployeeSystemSyncEntity;
import com.trainingmug.employee.enums.EmploymentStatus;
import com.trainingmug.employee.enums.IntegrationSystem;
import com.trainingmug.employee.enums.SyncStatus;
import com.trainingmug.employee.exception.EmployeeConflictException;
import com.trainingmug.employee.exception.EmployeeNotFoundException;
import com.trainingmug.employee.mapper.EmployeeMapper;
import com.trainingmug.employee.repository.EmployeeRepository;
import com.trainingmug.employee.repository.EmployeeSystemSyncRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** Applies employee profile, onboarding, employment, and synchronization rules. */
@Service
public class EmployeeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);
    private static final String EMPLOYEE_NOT_FOUND = "Employee not found";

    private final EmployeeRepository employees;
    private final EmployeeSystemSyncRepository syncs;
    private final EmployeeMapper mapper;

    public EmployeeService(
            EmployeeRepository employees,
            EmployeeSystemSyncRepository syncs,
            EmployeeMapper mapper
    ) {
        this.employees = employees;
        this.syncs = syncs;
        this.mapper = mapper;
    }

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        EmployeeEntity employee = buildEmployee(request);
        initializeSystemSyncStates(employee);
        try {
            EmployeeEntity savedEmployee = employees.save(employee);
            LOGGER.info("Created employee profile: {}", savedEmployee.getId());
            return mapper.toResponse(savedEmployee);
        } catch (DataIntegrityViolationException exception) {
            LOGGER.warn("Rejected employee creation because unique values already exist");
            throw new EmployeeConflictException("Employee number or email already exists");
        }
    }

    private EmployeeEntity buildEmployee(CreateEmployeeRequest request) {
        return new EmployeeEntity(
                request.employeeNumber().trim(),
                request.firstName().trim(),
                request.lastName().trim(),
                request.email().trim().toLowerCase(Locale.ROOT),
                request.jobTitle().trim(),
                request.department().trim(),
                request.managerName(),
                request.startDate());
    }

    private void initializeSystemSyncStates(EmployeeEntity employee) {
        for (IntegrationSystem system : IntegrationSystem.values()) {
            employee.addSync(system);
        }
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> listEmployees(String search) {
        String query = search == null || search.isBlank() ? "" : search.trim();
        return employees.search(query).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployee(UUID id) {
        return mapper.toResponse(findEmployee(id));
    }

    @Transactional
    public EmployeeResponse updateEmployee(UUID id, UpdateEmployeeRequest request) {
        EmployeeEntity employee = findEmployee(id);
        employee.updateProfile(
                request.firstName().trim(),
                request.lastName().trim(),
                request.email().trim().toLowerCase(Locale.ROOT),
                request.jobTitle().trim(),
                request.department().trim(),
                request.managerName(),
                request.startDate());
        try {
            employees.flush();
            LOGGER.info("Updated employee profile: {}", employee.getId());
            return mapper.toResponse(employee);
        } catch (DataIntegrityViolationException exception) {
            LOGGER.warn("Rejected employee update because email already exists");
            throw new EmployeeConflictException("Email already exists");
        }
    }

    @Transactional
    public EmployeeResponse advanceOnboardingStage(UUID id) {
        EmployeeEntity employee = employees.findByIdForUpdate(id)
                .orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND));
        try {
            employee.advance();
        } catch (IllegalStateException exception) {
            LOGGER.warn("Rejected onboarding advance for completed employee: {}", id);
            throw new EmployeeConflictException(exception.getMessage());
        }
        LOGGER.info("Advanced onboarding stage for employee: {}", id);
        return mapper.toResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateEmploymentStatus(UUID id, EmploymentStatus status) {
        EmployeeEntity employee = findEmployee(id);
        employee.setEmploymentStatus(status);
        LOGGER.info("Updated employment status for employee: {}", id);
        return mapper.toResponse(employee);
    }

    @Transactional
    public EmployeeResponse updateSystemSync(
            UUID id, IntegrationSystem system, SyncStatus status, String message
    ) {
        EmployeeEntity employee = findEmployee(id);
        validateSyncFailureMessage(status, message);
        String failureMessage = status == SyncStatus.FAILED ? message : null;
        EmployeeSystemSyncEntity sync = syncs.findByEmployeeIdAndSystemName(id, system)
                .orElseThrow(() -> new EmployeeNotFoundException("System sync not found"));
        sync.update(status, failureMessage);
        LOGGER.info("Updated {} sync status for employee: {}", system, id);
        return mapper.toResponse(employee);
    }

    private void validateSyncFailureMessage(SyncStatus status, String message) {
        if (status == SyncStatus.FAILED && (message == null || message.isBlank())) {
            LOGGER.warn("Rejected failed sync update without a failure message");
            throw new IllegalArgumentException("A failure message is required for FAILED syncs");
        }
    }

    private EmployeeEntity findEmployee(UUID id) {
        return employees.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND));
    }
}
