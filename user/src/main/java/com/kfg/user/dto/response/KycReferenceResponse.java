package com.kfg.user.dto.response;

import java.util.UUID;

public record KycReferenceResponse(
        UUID verificationId
) {
}
