package com.kfg.user.domain;

import java.util.Set;

public enum OnboardingStatus {
    DRAFT,
    READY_FOR_KYC,
    KYC_IN_PROGRESS,
    COMPLETED,
    REJECTED,
    CANCELED,
    EXPIRED;

    public static final Set<OnboardingStatus> ACTIVE_STATUSES = Set.of(DRAFT, READY_FOR_KYC, KYC_IN_PROGRESS);
    public boolean isTerminal() {
        return switch(this) {
            case COMPLETED, REJECTED, CANCELED, EXPIRED -> true;
            default -> false;
        };
    }
}
