package com.kfg.onboarding.dto.response;

import com.kfg.onboarding.domain.NextOnboardingStep;
import com.kfg.onboarding.domain.OnboardingStatus;

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
