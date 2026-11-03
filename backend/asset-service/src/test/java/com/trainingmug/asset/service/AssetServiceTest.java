package com.trainingmug.asset.service;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.trainingmug.asset.client.EmployeeClient;
import com.trainingmug.asset.dto.EmployeeServiceEmployeeResponse;
import com.trainingmug.asset.entity.AssetAssignmentEntity;
import com.trainingmug.asset.entity.AssetEntity;
import com.trainingmug.asset.enums.AssetLifecycleStatus;
import com.trainingmug.asset.enums.AssetType;
import com.trainingmug.asset.exception.AssetConflictException;
import com.trainingmug.asset.exception.EmployeeNotFoundException;
import com.trainingmug.asset.exception.EmployeeServiceUnavailableException;
import com.trainingmug.asset.mapper.AssetMapper;
import com.trainingmug.asset.repository.AssetAssignmentRepository;
import com.trainingmug.asset.repository.AssetRepository;
import com.trainingmug.asset.dto.CreateAssetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AssetServiceTest {
    @Mock
    AssetRepository assets;
    @Mock
    AssetAssignmentRepository assignments;
    @Mock
    EmployeeClient employees;
    @Mock
    TransactionTemplate transactionTemplate;
    AssetService service;

    @BeforeEach
    void setUp() {
        service = new AssetService(assets, assignments, employees,
                new AssetMapper(assignments), transactionTemplate);
        when(assignments.findFirstByAssetIdAndReturnedAtIsNull(any())).thenReturn(Optional.empty());
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(mock(TransactionStatus.class));
        });
    }

    @Test
    void createsAsset() {
        when(assets.save(any(AssetEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.createAsset(request());

        assertEquals("LAP-10", response.assetTag());
        assertEquals(AssetLifecycleStatus.AVAILABLE, response.lifecycleStatus());
    }

    @Test
    void duplicateAssetTagConflicts() {
        when(assets.save(any(AssetEntity.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint"));
        assertThrows(AssetConflictException.class, () -> service.createAsset(request()));
    }

    @Test
    void assignsAvailableAsset() {
        UUID assetId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        AssetEntity asset = asset();
        when(employees.get(employeeId)).thenReturn(new EmployeeServiceEmployeeResponse(employeeId, "ACTIVE"));
        when(assets.findByIdForUpdate(assetId)).thenReturn(Optional.of(asset));
        when(assignments.save(any(AssetAssignmentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.assignAsset(assetId, employeeId);

        assertEquals(AssetLifecycleStatus.CHECKED_OUT, asset.getLifecycleStatus());
        assertEquals(employeeId, asset.getEmployeeId());
        assertEquals(employeeId, result.employeeId());
        verify(assignments).save(any(AssetAssignmentEntity.class));
    }

    @Test
    void rejectsAssigningNonAvailableAssets() {
        assertAssignRejected(AssetLifecycleStatus.CHECKED_OUT);
    }

    @Test
    void rejectsAssigningMaintenanceAssets() {
        assertAssignRejected(AssetLifecycleStatus.MAINTENANCE_ALERT);
    }

    @Test
    void employeeServiceDownSurfacesUnavailableException() {
        UUID employeeId = UUID.randomUUID();
        when(employees.get(employeeId)).thenThrow(new EmployeeServiceUnavailableException());
        assertThrows(EmployeeServiceUnavailableException.class,
                () -> service.assignAsset(UUID.randomUUID(), employeeId));
    }

    @Test
    void missingEmployeeSurfacesNotFoundException() {
        UUID employeeId = UUID.randomUUID();
        when(employees.get(employeeId)).thenThrow(new EmployeeNotFoundException());
        assertThrows(EmployeeNotFoundException.class,
                () -> service.assignAsset(UUID.randomUUID(), employeeId));
    }

    @Test
    void returnMakesAssetAvailableAndClosesAssignment() {
        UUID id = UUID.randomUUID();
        AssetEntity asset = asset();
        asset.assign(UUID.randomUUID());
        AssetAssignmentEntity assignment = new AssetAssignmentEntity(asset, UUID.randomUUID());
        when(assets.findByIdForUpdate(id)).thenReturn(Optional.of(asset));
        when(assignments.findFirstByAssetIdAndReturnedAtIsNull(id)).thenReturn(Optional.of(assignment));

        var response = service.returnAsset(id);

        assertEquals(AssetLifecycleStatus.AVAILABLE, response.lifecycleStatus());
        assertNull(asset.getEmployeeId());
        assertNotNull(assignment.getReturnedAt());
    }

    @Test
    void marksAndResolvesMaintenance() {
        UUID id = UUID.randomUUID();
        AssetEntity asset = asset();
        when(assets.findByIdForUpdate(id)).thenReturn(Optional.of(asset));

        assertEquals(AssetLifecycleStatus.MAINTENANCE_ALERT,
                service.markForMaintenance(id, "Battery issue").lifecycleStatus());
        var response = service.resolveMaintenance(id);
        assertEquals(AssetLifecycleStatus.AVAILABLE, response.lifecycleStatus());
        assertNull(response.maintenanceReason());
    }

    @Test
    void resolvingNonMaintenanceAssetConflicts() {
        UUID id = UUID.randomUUID();
        when(assets.findByIdForUpdate(id)).thenReturn(Optional.of(asset()));
        assertThrows(AssetConflictException.class, () -> service.resolveMaintenance(id));
    }

    private void assertAssignRejected(AssetLifecycleStatus status) {
        UUID assetId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        AssetEntity asset = asset();
        if (status == AssetLifecycleStatus.MAINTENANCE_ALERT)
            asset.markMaintenance("Repair");
        else
            asset.assign(UUID.randomUUID());
        when(employees.get(employeeId)).thenReturn(new EmployeeServiceEmployeeResponse(employeeId, "ACTIVE"));
        when(assets.findByIdForUpdate(assetId)).thenReturn(Optional.of(asset));
        assertThrows(AssetConflictException.class, () -> service.assignAsset(assetId, employeeId));
        verify(assignments, never()).save(any(AssetAssignmentEntity.class));
    }

    private AssetEntity asset() {
        return new AssetEntity("LAP-10", AssetType.LAPTOP, "16GB RAM", LocalDate.now());
    }

    private CreateAssetRequest request() {
        return new CreateAssetRequest("LAP-10", AssetType.LAPTOP, "16GB RAM", LocalDate.now());
    }
}
