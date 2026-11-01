package com.trainingmug.asset.entity;
import com.trainingmug.asset.enums.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class AssetEntityTest {
  @Test void assignmentAndReturnKeepLifecycleConsistent(){var asset=new AssetEntity("AST-1",AssetType.LAPTOP,"16 GB RAM",LocalDate.now());UUID employee=UUID.randomUUID();asset.assign(employee);assertEquals(AssetLifecycleStatus.CHECKED_OUT,asset.getLifecycleStatus());assertEquals(employee,asset.getEmployeeId());asset.returnToInventory();assertEquals(AssetLifecycleStatus.AVAILABLE,asset.getLifecycleStatus());assertNull(asset.getEmployeeId());}
  @Test void maintenanceCannotRetainAnEmployee(){var asset=new AssetEntity("AST-2",AssetType.MONITOR,"4K",LocalDate.now());asset.assign(UUID.randomUUID());asset.markMaintenance("Panel repair");assertEquals(AssetLifecycleStatus.MAINTENANCE_ALERT,asset.getLifecycleStatus());assertNull(asset.getEmployeeId());assertEquals("Panel repair",asset.getMaintenanceReason());}
}
