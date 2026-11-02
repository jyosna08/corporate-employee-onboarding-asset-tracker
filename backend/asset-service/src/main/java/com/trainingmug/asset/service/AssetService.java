package com.trainingmug.asset.service;

import com.trainingmug.asset.client.EmployeeClient;
import com.trainingmug.asset.constants.AssetServiceConstants;
import com.trainingmug.asset.dto.AssignmentResponse;
import com.trainingmug.asset.dto.AssetResponse;
import com.trainingmug.asset.dto.CreateAssetRequest;
import com.trainingmug.asset.dto.UpdateAssetRequest;
import com.trainingmug.asset.entity.AssetAssignmentEntity;
import com.trainingmug.asset.entity.AssetEntity;
import com.trainingmug.asset.enums.AssetLifecycleStatus;
import com.trainingmug.asset.enums.AssetType;
import com.trainingmug.asset.exception.AssetConflictException;
import com.trainingmug.asset.exception.AssetNotFoundException;
import com.trainingmug.asset.exception.EmployeeNotFoundException;
import com.trainingmug.asset.exception.EmployeeServiceUnavailableException;
import com.trainingmug.asset.mapper.AssetMapper;
import com.trainingmug.asset.repository.AssetAssignmentRepository;
import com.trainingmug.asset.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

/** Applies asset inventory, assignment, return, and maintenance rules. */
@Service
public class AssetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssetService.class);
    private static final String ASSET_NOT_FOUND = "Asset not found";

    private final AssetRepository assets;
    private final AssetAssignmentRepository assignments;
    private final EmployeeClient employees;
    private final AssetMapper mapper;
    private final TransactionTemplate transactionTemplate;

    public AssetService(
            AssetRepository assets,
            AssetAssignmentRepository assignments,
            EmployeeClient employees,
            AssetMapper mapper,
            TransactionTemplate transactionTemplate
    ) {
        this.assets = assets;
        this.assignments = assignments;
        this.employees = employees;
        this.mapper = mapper;
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional(readOnly = true)
    public Page<AssetResponse> listAssets(
            String search, AssetLifecycleStatus status, AssetType type,
            int page, int size, String sort
    ) {
        validatePage(page, size);
        Pageable pageable = createPageRequest(page, size, sort);
        String query = search == null || search.isBlank() ? "" : search.trim();
        return assets.search(query, status, type, pageable).map(mapper::toResponse);
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size < 1 || size > AssetServiceConstants.MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "page must be >= 0 and size must be between 1 and 100");
        }
    }

    private Pageable createPageRequest(int page, int size, String sort) {
        String sortValue = sort == null ? AssetServiceConstants.DEFAULT_ASSET_SORT : sort;
        String[] parts = sortValue.split(",");
        String property = resolveSortProperty(parts[0]);
        Sort.Direction direction = parts.length == 2
                ? Sort.Direction.fromString(parts[1])
                : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, property));
    }

    private String resolveSortProperty(String property) {
        return switch (property) {
            case "assetTag" -> "assetTag";
            case "type" -> "type";
            case "purchaseDate" -> "purchaseDate";
            case "lifecycleStatus" -> "lifecycleStatus";
            default -> throw new IllegalArgumentException("Unsupported asset sort field");
        };
    }

    @Transactional(readOnly = true)
    public AssetResponse getAsset(UUID id) {
        return mapper.toResponse(findAsset(id));
    }

    @Transactional
    public AssetResponse createAsset(CreateAssetRequest request) {
        validatePurchaseDate(request.purchaseDate());
        AssetEntity asset = new AssetEntity(
                request.assetTag().trim(), request.type(),
                request.hardwareSpecs().trim(), request.purchaseDate());
        try {
            AssetEntity savedAsset = assets.save(asset);
            LOGGER.info("Registered asset: {}", savedAsset.getId());
            return mapper.toResponse(savedAsset);
        } catch (DataIntegrityViolationException exception) {
            LOGGER.warn("Rejected asset creation because asset tag already exists");
            throw new AssetConflictException("Asset tag already exists");
        }
    }

    @Transactional
    public AssetResponse updateAsset(UUID id, UpdateAssetRequest request) {
        AssetEntity asset = findAssetForUpdate(id);
        if (asset.getLifecycleStatus() == AssetLifecycleStatus.CHECKED_OUT) {
            throw new AssetConflictException("Return an assigned asset before editing it");
        }
        validatePurchaseDate(request.purchaseDate());
        asset.update(request.assetTag().trim(), request.type(),
                request.hardwareSpecs().trim(), request.purchaseDate());
        return saveUpdatedAsset(asset);
    }

    private AssetResponse saveUpdatedAsset(AssetEntity asset) {
        try {
            assets.flush();
            LOGGER.info("Updated asset: {}", asset.getId());
            return mapper.toResponse(asset);
        } catch (DataIntegrityViolationException exception) {
            LOGGER.warn("Rejected asset update because asset tag already exists");
            throw new AssetConflictException("Asset tag already exists");
        }
    }

    /** Only available assets can be assigned to active employees. */
    public AssignmentResponse assignAsset(UUID assetId, UUID employeeId) {
        validateEmployeeActive(employeeId);
        return transactionTemplate.execute(
                transactionStatus -> assignAvailableAsset(assetId, employeeId));
    }

    private AssignmentResponse assignAvailableAsset(UUID assetId, UUID employeeId) {
        AssetEntity asset = findAssetForUpdate(assetId);
        validateAssetAvailable(asset);
        asset.assign(employeeId);
        AssetAssignmentEntity assignment = assignments.save(
                new AssetAssignmentEntity(asset, employeeId));
        LOGGER.info("Assigned asset {} to employee {}", assetId, employeeId);
        return mapper.toResponse(assignment);
    }

    private void validateAssetAvailable(AssetEntity asset) {
        if (asset.getLifecycleStatus() != AssetLifecycleStatus.AVAILABLE) {
            LOGGER.warn("Rejected assignment for unavailable asset: {}", asset.getId());
            throw new AssetConflictException("Only available assets can be assigned");
        }
    }

    private void validateEmployeeActive(UUID employeeId) {
        try {
            var employee = employees.get(employeeId);
            if (employee == null) {
                throw new EmployeeNotFoundException();
            }
            if (!AssetServiceConstants.ACTIVE_EMPLOYMENT_STATUS.equals(
                    employee.employmentStatus())) {
                LOGGER.warn("Rejected assignment to inactive employee: {}", employeeId);
                throw new AssetConflictException(
                        "Assets cannot be assigned to a terminated employee");
            }
        } catch (EmployeeNotFoundException | EmployeeServiceUnavailableException exception) {
            throw exception;
        }
    }

    @Transactional
    public AssetResponse returnAsset(UUID id) {
        AssetEntity asset = findAssetForUpdate(id);
        if (asset.getLifecycleStatus() != AssetLifecycleStatus.CHECKED_OUT) {
            throw new AssetConflictException("Asset has no open assignment");
        }
        AssetAssignmentEntity assignment = assignments
                .findFirstByAssetIdAndReturnedAtIsNull(id)
                .orElseThrow(() -> new AssetConflictException(
                        "Open assignment history was not found"));
        assignment.close();
        asset.returnToInventory();
        LOGGER.info("Returned asset to inventory: {}", id);
        return mapper.toResponse(asset);
    }

    @Transactional
    public AssetResponse markForMaintenance(UUID id, String reason) {
        AssetEntity asset = findAssetForUpdate(id);
        if (asset.getLifecycleStatus() == AssetLifecycleStatus.CHECKED_OUT) {
            throw new AssetConflictException("Return the asset before marking maintenance");
        }
        asset.markMaintenance(reason.trim());
        LOGGER.info("Marked asset for maintenance: {}", id);
        return mapper.toResponse(asset);
    }

    /** Only a maintenance alert can be resolved back into available inventory. */
    @Transactional
    public AssetResponse resolveMaintenance(UUID id) {
        AssetEntity asset = findAssetForUpdate(id);
        if (asset.getLifecycleStatus() != AssetLifecycleStatus.MAINTENANCE_ALERT) {
            throw new AssetConflictException("Only assets in maintenance can be resolved");
        }
        asset.resolveMaintenance();
        LOGGER.info("Resolved maintenance for asset: {}", id);
        return mapper.toResponse(asset);
    }

    @Transactional(readOnly = true)
    public Page<AssignmentResponse> getAssignmentHistory(UUID id, int page, int size) {
        if (!assets.existsById(id)) {
            throw new AssetNotFoundException(ASSET_NOT_FOUND);
        }
        validatePage(page, size);
        return assignments.findByAssetIdOrderByAssignedAtDesc(
                id, PageRequest.of(page, size)).map(mapper::toResponse);
    }

    private void validatePurchaseDate(LocalDate purchaseDate) {
        if (purchaseDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Purchase date cannot be in the future");
        }
    }

    private AssetEntity findAsset(UUID id) {
        return assets.findById(id)
                .orElseThrow(() -> new AssetNotFoundException(ASSET_NOT_FOUND));
    }

    private AssetEntity findAssetForUpdate(UUID id) {
        return assets.findByIdForUpdate(id)
                .orElseThrow(() -> new AssetNotFoundException(ASSET_NOT_FOUND));
    }
}
