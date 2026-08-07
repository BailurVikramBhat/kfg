package com.kfg.user.dto.kyc;

import com.kfg.user.domain.KycStatus;

public record KycVerificationResult(KycStatus status,
                                    String remarks) {
}
