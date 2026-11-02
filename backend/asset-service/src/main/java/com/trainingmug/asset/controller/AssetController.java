package com.trainingmug.asset.controller;

import com.trainingmug.asset.constants.AssetServiceConstants;
import com.trainingmug.asset.dto.AssignAssetRequest;
import com.trainingmug.asset.dto.AssignmentResponse;
import com.trainingmug.asset.dto.AssetResponse;
import com.trainingmug.asset.dto.CreateAssetRequest;
import com.trainingmug.asset.dto.MaintenanceRequest;
import com.trainingmug.asset.dto.UpdateAssetRequest;
import com.trainingmug.asset.enums.AssetLifecycleStatus;
import com.trainingmug.asset.enums.AssetType;
import com.trainingmug.asset.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

/** REST endpoints for asset inventory and assignment history. */
@RestController
@RequestMapping("/api/assets")
public class AssetController {
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @Operation(summary = "Register an available asset")
    @PostMapping
    public ResponseEntity<AssetResponse> create(
            @Valid @RequestBody CreateAssetRequest request
    ) {
        AssetResponse created = assetService.createAsset(request);
        URI location = URI.create("/api/assets/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public Page<AssetResponse> list(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) AssetLifecycleStatus status,
            @RequestParam(value = "type", required = false) AssetType type,
            @RequestParam(value = "page", defaultValue = AssetServiceConstants.DEFAULT_PAGE_VALUE) int page,
            @RequestParam(value = "size", defaultValue = AssetServiceConstants.DEFAULT_PAGE_SIZE_VALUE) int size,
            @RequestParam(value = "sort", defaultValue = AssetServiceConstants.DEFAULT_ASSET_SORT)
            String sort
    ) {
        return assetService.listAssets(search, status, type, page, size, sort);
    }

    @GetMapping("/{id}")
    public AssetResponse get(@PathVariable("id") UUID id) {
        return assetService.getAsset(id);
    }

    @PutMapping("/{id}")
    public AssetResponse update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateAssetRequest request
    ) {
        return assetService.updateAsset(id, request);
    }

    @Operation(summary = "Assign an available asset to an active employee")
    @PostMapping("/{id}/assignments")
    public ResponseEntity<AssignmentResponse> assign(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AssignAssetRequest request
    ) {
        AssignmentResponse assignment = assetService.assignAsset(id, request.employeeId());
        return ResponseEntity.status(201).body(assignment);
    }

    @PostMapping("/{id}/return")
    public AssetResponse returnAsset(@PathVariable("id") UUID id) {
        return assetService.returnAsset(id);
    }

    @PostMapping("/{id}/maintenance")
    public AssetResponse maintenance(
            @PathVariable("id") UUID id,
            @Valid @RequestBody MaintenanceRequest request
    ) {
        return assetService.markForMaintenance(id, request.reason());
    }

    @Operation(summary = "Resolve maintenance and return an asset to available inventory")
    @PostMapping("/{id}/maintenance/resolve")
    public AssetResponse resolveMaintenance(@PathVariable("id") UUID id) {
        return assetService.resolveMaintenance(id);
    }

    @GetMapping("/{id}/assignments")
    public Page<AssignmentResponse> history(
            @PathVariable("id") UUID id,
            @RequestParam(value = "page", defaultValue = AssetServiceConstants.DEFAULT_PAGE_VALUE) int page,
            @RequestParam(value = "size", defaultValue = AssetServiceConstants.DEFAULT_PAGE_SIZE_VALUE) int size
    ) {
        return assetService.getAssignmentHistory(id, page, size);
    }
}
