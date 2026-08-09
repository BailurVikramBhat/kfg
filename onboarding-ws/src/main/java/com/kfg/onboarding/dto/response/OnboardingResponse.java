package com.kfg.onboarding.dto.response;


import com.kfg.onboarding.domain.NextOnboardingStep;
import com.kfg.onboarding.domain.OnboardingAction;
import com.kfg.onboarding.domain.OnboardingStatus;


import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record OnboardingResponse(

        UUID applicationId,

        OnboardingStatus status,

        NextOnboardingStep nextStep,

        Set<OnboardingAction> allowedActions,

        BasicDetailsResponse basicDetails,

        ResidentialAddressResponse residentialAddress,

        KycReferenceResponse kyc,

        long version,

        Instant createdAt,

        Instant updatedAt
) {
}
