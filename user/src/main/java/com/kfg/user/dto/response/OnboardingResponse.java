package com.kfg.user.dto.response;


import com.kfg.user.domain.NextOnboardingStep;
import com.kfg.user.domain.OnboardingAction;
import com.kfg.user.domain.OnboardingStatus;


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
