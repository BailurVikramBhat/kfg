package com.kfg.user.dto.response;

import com.kfg.user.domain.NextOnboardingStep;
import com.kfg.user.domain.OnboardingStatus;

import java.time.Instant;
import java.util.UUID;

public record OnboardingApplicationSummary(
        UUID applicationId,
        OnboardingStatus status,
        NextOnboardingStep nextStep,
        String applicantName,
        String email,
        String phoneNumber,
        Instant createdAt,
        Instant updatedAt
) {
}
