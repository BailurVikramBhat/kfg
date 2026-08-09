package com.kfg.onboarding.strategy.kyc;


import com.kfg.onboarding.domain.KycStatus;
import com.kfg.onboarding.domain.KycType;
import com.kfg.onboarding.dto.kyc.KycVerificationContext;
import com.kfg.onboarding.dto.kyc.KycVerificationResult;
import org.springframework.stereotype.Component;

@Component
public class StandardKycStrategy implements KycVerificationStrategy{

    @Override
    public KycType supportedType() {
        return KycType.STANDARD;
    }

    @Override
    public KycVerificationResult verify(KycVerificationContext context) {
        return new KycVerificationResult(KycStatus.PENDING, "Standard Kyc verification initiated");
    }
}
