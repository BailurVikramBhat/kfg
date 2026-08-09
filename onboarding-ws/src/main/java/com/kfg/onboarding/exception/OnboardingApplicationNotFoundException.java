package com.kfg.onboarding.exception;

import java.util.UUID;

public class OnboardingApplicationNotFoundException extends RuntimeException {

    public OnboardingApplicationNotFoundException(UUID id) {
        super("Onboarding application not found with ID: " + id);
    }
}
