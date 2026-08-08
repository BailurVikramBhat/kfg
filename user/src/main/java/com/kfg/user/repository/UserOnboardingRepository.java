package com.kfg.user.repository;

import com.kfg.user.domain.OnboardingStatus;
import com.kfg.user.domain.UserOnboardingApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserOnboardingRepository extends JpaRepository<UserOnboardingApplication, UUID> {

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

    Optional<UserOnboardingApplication>
    findByKycVerificationId(UUID kycVerificationId);
}
