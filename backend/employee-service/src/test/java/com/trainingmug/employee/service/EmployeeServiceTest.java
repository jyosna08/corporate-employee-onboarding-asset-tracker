package com.trainingmug.employee.service;

import com.trainingmug.employee.dto.CreateEmployeeRequest;
import com.trainingmug.employee.entity.EmployeeEntity;
import com.trainingmug.employee.enums.IntegrationSystem;
import com.trainingmug.employee.enums.OnboardingStatus;
import com.trainingmug.employee.exception.EmployeeConflictException;
import com.trainingmug.employee.mapper.EmployeeMapper;
import com.trainingmug.employee.repository.EmployeeRepository;
import com.trainingmug.employee.repository.EmployeeSystemSyncRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock EmployeeRepository employees;
    @Mock EmployeeSystemSyncRepository syncs;
    EmployeeService service;

    @BeforeEach void setUp() {
        service = new EmployeeService(employees, syncs, new EmployeeMapper());
    }

    @Test void createsEmployeeAndInitializesSyncStates() {
        when(employees.save(any(EmployeeEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.createEmployee(request());

        ArgumentCaptor<EmployeeEntity> captor = ArgumentCaptor.forClass(EmployeeEntity.class);
        verify(employees).save(captor.capture());
        assertEquals("EMP-100", response.employeeNumber());
        assertEquals("alex@example.com", response.email());
        assertEquals(OnboardingStatus.PENDING_REVIEW, response.onboardingStatus());
        assertEquals(IntegrationSystem.values().length, captor.getValue().getSystemSyncs().size());
    }

    @Test void duplicateEmployeeNumberOrEmailConflicts() {
        when(employees.save(any(EmployeeEntity.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint"));

        assertThrows(EmployeeConflictException.class, () -> service.createEmployee(request()));
        assertThrows(EmployeeConflictException.class, () -> service.createEmployee(
                new CreateEmployeeRequest("EMP-101", "Alex", "Morgan", "alex@example.com",
                        "Engineer", "IT", null, LocalDate.now())));
    }

    @Test void rejectsAdvanceAfterVerification() {
        UUID id = UUID.randomUUID();
        EmployeeEntity employee = employee();
        employee.advance();
        employee.advance();
        when(employees.findByIdForUpdate(id)).thenReturn(Optional.of(employee));

        assertThrows(EmployeeConflictException.class, () -> service.advanceOnboardingStage(id));
        assertEquals(OnboardingStatus.VERIFIED, employee.getOnboardingStatus());
    }

    @Test void advancesPendingReviewThroughInProgressToVerified() {
        UUID id = UUID.randomUUID();
        EmployeeEntity employee = employee();
        when(employees.findByIdForUpdate(id)).thenReturn(Optional.of(employee));

        assertEquals(OnboardingStatus.IN_PROGRESS, service.advanceOnboardingStage(id).onboardingStatus());
        assertEquals(OnboardingStatus.VERIFIED, service.advanceOnboardingStage(id).onboardingStatus());
    }

    private CreateEmployeeRequest request() {
        return new CreateEmployeeRequest(" EMP-100 ", "Alex", "Morgan", "Alex@Example.com",
                "Engineer", "IT", null, LocalDate.now());
    }

    private EmployeeEntity employee() {
        EmployeeEntity employee = new EmployeeEntity("EMP-100", "Alex", "Morgan",
                "alex@example.com", "Engineer", "IT", null, LocalDate.now());
        for (IntegrationSystem system : IntegrationSystem.values()) employee.addSync(system);
        return employee;
    }
}
