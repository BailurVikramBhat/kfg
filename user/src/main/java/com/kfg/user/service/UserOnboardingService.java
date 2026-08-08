package com.kfg.user.service;

import com.kfg.user.dto.SaveResidentialAddressCommand;
import com.kfg.user.dto.StartOnboardingCommand;
import com.kfg.user.dto.UpdateBasicDetailsCommand;
import com.kfg.user.dto.response.OnboardingApplicationSummary;
import com.kfg.user.dto.response.OnboardingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserOnboardingService {

    Page<OnboardingApplicationSummary> findAll(Pageable pageable);

    OnboardingResponse start(StartOnboardingCommand command);

    OnboardingResponse getById(UUID applicationId);

    OnboardingResponse updateBasicDetails(UUID applicationId, UpdateBasicDetailsCommand command);

    OnboardingResponse saveResidentialAddress(UUID applicationId, SaveResidentialAddressCommand command);

    OnboardingResponse cancel(UUID applicationId);
}
