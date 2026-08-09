package com.kfg.onboarding.strategy.kyc;

import com.kfg.onboarding.domain.KycStatus;
import com.kfg.onboarding.domain.KycType;
import com.kfg.onboarding.dto.kyc.KycVerificationContext;
import com.kfg.onboarding.dto.kyc.KycVerificationResult;
import org.springframework.stereotype.Component;

@Component
public class EnhancedKycStrategy implements KycVerificationStrategy {

    @Override
    public KycType supportedType() {
        return KycType.ENHANCED;
    }

    @Override
    public KycVerificationResult verify(
            KycVerificationContext context
    ) {
        return new KycVerificationResult(
                KycStatus.PENDING,
                "Enhanced KYC initiated"
        );
    }
}
