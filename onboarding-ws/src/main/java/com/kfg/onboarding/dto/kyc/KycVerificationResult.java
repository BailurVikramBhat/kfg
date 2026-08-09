package com.kfg.onboarding.dto.kyc;

import com.kfg.onboarding.domain.KycStatus;

public record KycVerificationResult(KycStatus status,
                                    String remarks) {
}
