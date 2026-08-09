package com.kfg.onboarding.repository;

import com.kfg.onboarding.domain.OnboardingStatus;
import com.kfg.onboarding.domain.OnboardingApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;



public interface OnboardingApplicationRepository extends JpaRepository<OnboardingApplication, UUID> {

    interface StatusCount {
        OnboardingStatus getStatus();
        long getCount();
    }

    @Query("SELECT a.status as status, COUNT(a) as count FROM OnboardingApplication a GROUP BY a.status")
    List<StatusCount> countGroupedByStatus();

    @Query("""
                SELECT application
                FROM OnboardingApplication application
                WHERE application.status IN :activeStatuses
                AND (
                    LOWER(application.basicDetails.email) = LOWER(:email)
                    OR application.basicDetails.phoneNumber = :phoneNumber
                    )
            """)
    List<OnboardingApplication> findActiveConflicts(
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("activeStatuses") Collection<OnboardingStatus> activeStatuses);

    @Query("""
                SELECT application
                FROM OnboardingApplication application
                WHERE application.id <> :applicationId
                AND application.status IN :activeStatuses
                AND (
                    LOWER(application.basicDetails.email) = LOWER(:email)
                    OR application.basicDetails.phoneNumber = :phoneNumber
                    )
            """)
    List<OnboardingApplication> findOtherActiveApplicationsWithContactDetails(
            @Param("applicationId") UUID applicationId,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("activeStatuses") Collection<OnboardingStatus> activeStatuses
            );
}
