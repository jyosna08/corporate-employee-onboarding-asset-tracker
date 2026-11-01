package com.trainingmug.employee.repository;

import com.trainingmug.employee.entity.EmployeeEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Employee persistence and search queries. */
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {
    @EntityGraph(attributePaths = "systemSyncs")
    @Query("select e from EmployeeEntity e where "
            + "lower(e.firstName) like lower(concat('%', :search, '%')) or "
            + "lower(e.lastName) like lower(concat('%', :search, '%')) or "
            + "lower(e.email) like lower(concat('%', :search, '%')) or "
            + "lower(e.employeeNumber) like lower(concat('%', :search, '%')) "
            + "order by e.createdAt desc")
    List<EmployeeEntity> search(@Param("search") String search);

    /** Locks the profile while onboarding state advances to prevent lost updates. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "systemSyncs")
    @Query("select e from EmployeeEntity e where e.id = :id")
    Optional<EmployeeEntity> findByIdForUpdate(@Param("id") UUID id);

    boolean existsByEmailIgnoreCase(String email);
}
