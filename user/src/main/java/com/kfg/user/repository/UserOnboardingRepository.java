package com.kfg.user.repository;

import com.kfg.user.domain.OnboardingStatus;
import com.kfg.user.domain.UserOnboardingApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;



public interface UserOnboardingRepository extends JpaRepository<UserOnboardingApplication, UUID> {

    interface StatusCount {
        OnboardingStatus getStatus();
        long getCount();
    }

    @Query("SELECT a.status as status, COUNT(a) as count FROM UserOnboardingApplication a GROUP BY a.status")
    List<StatusCount> countGroupedByStatus();

    @Query("""
                SELECT application
                FROM UserOnboardingApplication application
                WHERE application.status IN :activeStatuses
                AND (
                    LOWER(application.basicDetails.email) = LOWER(:email)
                    OR application.basicDetails.phoneNumber = :phoneNumber
                    )
            """)
    List<UserOnboardingApplication> findActiveConflicts(
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("activeStatuses") Collection<OnboardingStatus> activeStatuses);
}
