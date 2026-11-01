package com.trainingmug.employee.entity;
import com.trainingmug.employee.enums.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class EmployeeEntityTest {
  @Test void onboardingAdvancesOnlyThroughAllowedStages(){var employee=new EmployeeEntity("TM-1","Ava","Stone","ava@example.com","Engineer","IT",null,LocalDate.now());assertEquals(OnboardingStatus.PENDING_REVIEW,employee.getOnboardingStatus());employee.advance();assertEquals(OnboardingStatus.IN_PROGRESS,employee.getOnboardingStatus());employee.advance();assertEquals(OnboardingStatus.VERIFIED,employee.getOnboardingStatus());assertThrows(IllegalStateException.class,employee::advance);}
  @Test void employmentStatusIsIndependentOfOnboarding(){var employee=new EmployeeEntity("TM-2","Noah","Lee","noah@example.com","Analyst","Finance",null,LocalDate.now());employee.setEmploymentStatus(EmploymentStatus.TERMINATED);assertEquals(EmploymentStatus.TERMINATED,employee.getEmploymentStatus());assertEquals(OnboardingStatus.PENDING_REVIEW,employee.getOnboardingStatus());}
}
