package com.kfg.onboarding.service;

import com.kfg.onboarding.domain.BasicDetails;
import com.kfg.onboarding.domain.OnboardingStatus;
import com.kfg.onboarding.domain.ResidentialAddress;
import com.kfg.onboarding.domain.OnboardingApplication;
import com.kfg.onboarding.dto.SaveResidentialAddressCommand;
import com.kfg.onboarding.dto.StartOnboardingCommand;
import com.kfg.onboarding.dto.UpdateBasicDetailsCommand;
import com.kfg.onboarding.dto.response.OnboardingApplicationSummary;
import com.kfg.onboarding.dto.response.OnboardingResponse;
import com.kfg.onboarding.exception.CountryNotEnabledException;
import com.kfg.onboarding.repository.CountryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.kfg.onboarding.exception.DuplicateResourceException;
import com.kfg.onboarding.exception.OnboardingApplicationNotFoundException;
import com.kfg.onboarding.mapper.OnboardingMapper;
import com.kfg.onboarding.repository.OnboardingApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OnboardingServiceImpl implements OnboardingService {

    private final OnboardingApplicationRepository repository;
    private final CountryRepository countryRepository;
    private final OnboardingMapper mapper;

    @Override
    public Page<OnboardingApplicationSummary> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toSummary);
    }

    @Override
    @Transactional
    public OnboardingResponse start(StartOnboardingCommand command) {
        List<OnboardingApplication> conflicts = repository.findActiveConflicts(
                command.email(),
                command.phoneNumber(),
                OnboardingStatus.ACTIVE_STATUSES
        );
        if (!conflicts.isEmpty()) {
            throw new DuplicateResourceException(
                    "An active onboarding application already exists for this email or phone number."
            );
        }
        BasicDetails basicDetails = new BasicDetails(
                command.firstName(), command.lastName(), command.email(), command.phoneNumber()
        );
        return mapper.toResponse(repository.save(OnboardingApplication.start(basicDetails)));
    }

    @Override
    public OnboardingResponse getById(UUID applicationId) {
        return repository.findById(applicationId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new OnboardingApplicationNotFoundException(applicationId));
    }

    @Override
    @Transactional
    public OnboardingResponse updateBasicDetails(UUID applicationId, UpdateBasicDetailsCommand command) {
        OnboardingApplication application = findOrThrow(applicationId);
        List<OnboardingApplication> conflicts = repository.findOtherActiveApplicationsWithContactDetails(
                applicationId,
                command.email(),
                command.phoneNumber(),
                OnboardingStatus.ACTIVE_STATUSES
        );
        if (!conflicts.isEmpty()) {
            throw new DuplicateResourceException(
                    "An active onboarding application already exists for this email or phone number."
            );
        }

        application.updateBasicDetails(
                new BasicDetails(command.firstName(), command.lastName(), command.email(), command.phoneNumber())
        );
        return mapper.toResponse(application);
    }

    @Override
    @Transactional
    public OnboardingResponse saveResidentialAddress(UUID applicationId, SaveResidentialAddressCommand command) {
        OnboardingApplication application = findOrThrow(applicationId);
        String countryCode = command.countryCode();
        if(!countryRepository.existsByCodeAndEnabledTrue(countryCode)) {
            throw new CountryNotEnabledException(countryCode + " is not enabled for operation");
        }
        application.saveResidentialAddress(
                new ResidentialAddress(
                        command.addressLine1(), command.addressLine2(), command.locality(),
                        command.city(), command.state(), command.postalCode(), command.countryCode()
                )
        );
        return mapper.toResponse(application);
    }

    @Override
    @Transactional
    public OnboardingResponse cancel(UUID applicationId) {
        OnboardingApplication application = findOrThrow(applicationId);
        application.cancel(LocalDateTime.now());
        return mapper.toResponse(application);
    }

    private OnboardingApplication findOrThrow(UUID applicationId) {
        return repository.findById(applicationId)
                .orElseThrow(() -> new OnboardingApplicationNotFoundException(applicationId));
    }
}
