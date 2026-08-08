package com.kfg.user.domain;

import com.kfg.user.exception.InvalidOnboardingStateException;
import com.kfg.user.exception.KycPrerequisiteException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_onboarding_applications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserOnboardingApplication extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private long version;

    @Embedded
    private BasicDetails basicDetails;

    @Embedded
    private ResidentialAddress residentialAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnboardingStatus status;

    private LocalDateTime kycStartedAt;

    private LocalDateTime completedAt;

    private LocalDateTime cancelledAt;

    private void ensureDraft() {
        if (status != OnboardingStatus.DRAFT) {
            throw new InvalidOnboardingStateException(
                    "Application cannot be modified in status: " + status
            );
        }
    }

    public static UserOnboardingApplication start(BasicDetails basicDetails) {
        UserOnboardingApplication application = new UserOnboardingApplication();
        application.basicDetails = basicDetails;
        application.status = OnboardingStatus.DRAFT;
        return application;
    }

    public void updateBasicDetails(BasicDetails basicDetails) {
        ensureDraft();
        this.basicDetails = basicDetails;
    }

    public void saveResidentialAddress(ResidentialAddress residentialAddress) {
        ensureDraft();
        this.residentialAddress = residentialAddress;
    }

    public void beginKyc(LocalDateTime startedAt) {
        ensureDraft();

        if (residentialAddress == null) {
            throw new KycPrerequisiteException(
                    "Residential address must be captured before KYC"
            );
        }
        status = OnboardingStatus.KYC_IN_PROGRESS;
        kycStartedAt = startedAt;
    }

    public void complete(LocalDateTime completedAt) {
        if (status != OnboardingStatus.KYC_IN_PROGRESS) {
            throw new InvalidOnboardingStateException(
                    "Only an application under KYC review can be completed"
            );
        }

        status = OnboardingStatus.COMPLETED;
        this.completedAt = completedAt;
    }

    public void reject() {
        if (status != OnboardingStatus.KYC_IN_PROGRESS) {
            throw new InvalidOnboardingStateException(
                    "Only an application under KYC review can be rejected"
            );
        }

        status = OnboardingStatus.REJECTED;
    }

    public void cancel(LocalDateTime cancelledAt) {
        ensureDraft();
        status = OnboardingStatus.CANCELED;
        this.cancelledAt = cancelledAt;
    }

}
