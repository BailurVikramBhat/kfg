package com.kfg.onboarding.strategy.kyc;

import com.kfg.onboarding.domain.KycType;
import com.kfg.onboarding.dto.kyc.KycVerificationContext;
import com.kfg.onboarding.dto.kyc.KycVerificationResult;

public interface KycVerificationStrategy {
    KycType supportedType();

    KycVerificationResult verify(KycVerificationContext context);
}
