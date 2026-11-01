package com.trainingmug.employee.entity;

import com.trainingmug.employee.enums.EmploymentStatus;
import com.trainingmug.employee.enums.IntegrationSystem;
import com.trainingmug.employee.enums.OnboardingStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Persistent employee profile and onboarding state. */
@Entity
@Table(name = "employees", indexes = {
        @Index(name = "ix_employees_pipeline", columnList = "onboarding_status,start_date")
})
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_number", nullable = false, unique = true, length = 32)
    private String employeeNumber;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "job_title", nullable = false, length = 120)
    private String jobTitle;

    @Column(nullable = false, length = 120)
    private String department;

    @Column(name = "manager_name", length = 200)
    private String managerName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "onboarding_status", nullable = false, length = 24)
    private OnboardingStatus onboardingStatus = OnboardingStatus.PENDING_REVIEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 16)
    private EmploymentStatus employmentStatus = EmploymentStatus.ACTIVE;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EmployeeSystemSyncEntity> systemSyncs = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    protected EmployeeEntity() {
    }

    public EmployeeEntity(
            String employeeNumber, String firstName, String lastName, String email,
            String jobTitle, String department, String managerName, LocalDate startDate
    ) {
        this.employeeNumber = employeeNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.jobTitle = jobTitle;
        this.department = department;
        this.managerName = managerName;
        this.startDate = startDate;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getEmployeeNumber() { return employeeNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getJobTitle() { return jobTitle; }
    public String getDepartment() { return department; }
    public String getManagerName() { return managerName; }
    public LocalDate getStartDate() { return startDate; }
    public OnboardingStatus getOnboardingStatus() { return onboardingStatus; }
    public EmploymentStatus getEmploymentStatus() { return employmentStatus; }
    public List<EmployeeSystemSyncEntity> getSystemSyncs() { return systemSyncs; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void updateProfile(
            String firstName, String lastName, String email, String jobTitle,
            String department, String managerName, LocalDate startDate
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.jobTitle = jobTitle;
        this.department = department;
        this.managerName = managerName;
        this.startDate = startDate;
    }

    /** Enforces the onboarding sequence and rejects advances after verification. */
    public void advance() {
        onboardingStatus = switch (onboardingStatus) {
            case PENDING_REVIEW -> OnboardingStatus.IN_PROGRESS;
            case IN_PROGRESS -> OnboardingStatus.VERIFIED;
            case VERIFIED -> throw new IllegalStateException("Employee is already VERIFIED");
        };
    }

    public void setEmploymentStatus(EmploymentStatus status) {
        employmentStatus = status;
    }

    public void addSync(IntegrationSystem system) {
        systemSyncs.add(new EmployeeSystemSyncEntity(this, system));
    }
}
