package com.kfg.onboarding.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "kyc_verifications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_kyc_application_attempt",
                        columnNames = {
                                "onboarding_application_id",
                                "attempt_number"
                        }
                )
        }
)
public class KycVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID onboardingApplicationId;

    private int attemptNumber;

    @Enumerated(EnumType.STRING)
    private KycType type;

    @Enumerated(EnumType.STRING)
    private KycStatus status;

    private String remarks;

    private LocalDateTime decidedAt;

    @Version
    private long version;
}
