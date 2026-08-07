package com.kfg.user.strategy.kyc;

import com.kfg.user.domain.KycType;
import com.kfg.user.dto.kyc.KycVerificationContext;
import com.kfg.user.dto.kyc.KycVerificationResult;

public interface KycVerificationStrategy {
    KycType supportedType();

    KycVerificationResult verify(KycVerificationContext context);
}
