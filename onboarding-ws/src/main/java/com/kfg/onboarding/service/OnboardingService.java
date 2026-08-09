package com.kfg.onboarding.service;

import com.kfg.onboarding.dto.SaveResidentialAddressCommand;
import com.kfg.onboarding.dto.StartOnboardingCommand;
import com.kfg.onboarding.dto.UpdateBasicDetailsCommand;
import com.kfg.onboarding.dto.response.OnboardingApplicationSummary;
import com.kfg.onboarding.dto.response.OnboardingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OnboardingService {

    Page<OnboardingApplicationSummary> findAll(Pageable pageable);

    OnboardingResponse start(StartOnboardingCommand command);

    OnboardingResponse getById(UUID applicationId);

    OnboardingResponse updateBasicDetails(UUID applicationId, UpdateBasicDetailsCommand command);

    OnboardingResponse saveResidentialAddress(UUID applicationId, SaveResidentialAddressCommand command);

    OnboardingResponse cancel(UUID applicationId);
}
