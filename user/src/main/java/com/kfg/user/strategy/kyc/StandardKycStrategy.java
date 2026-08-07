package com.kfg.user.strategy.kyc;


import com.kfg.user.domain.KycStatus;
import com.kfg.user.domain.KycType;
import com.kfg.user.dto.kyc.KycVerificationContext;
import com.kfg.user.dto.kyc.KycVerificationResult;
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
