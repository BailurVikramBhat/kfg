package com.kfg.user.service;

import com.kfg.user.dto.response.OnboardingResponse;

import java.util.UUID;

public interface UserOnboardingService {
    OnboardingResponse start(
            StartOnboardingCommand command
    );

    OnboardingResponse getById(UUID applicationId);

    OnboardingResponse updateBasicDetails(
            UUID applicationId,
            UpdateBasicDetailsCommand command
    );

    OnboardingResponse saveResidentialAddress(
            UUID applicationId,
            SaveResidentialAddressCommand command
    );

    KycVerificationResponse initiateKyc(
            UUID applicationId,
            InitiateKycCommand command
    );

    OnboardingResponse cancel(UUID applicationId);
}
