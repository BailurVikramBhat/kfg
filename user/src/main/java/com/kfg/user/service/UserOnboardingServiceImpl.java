package com.kfg.user.service;

import com.kfg.user.domain.BasicDetails;
import com.kfg.user.domain.OnboardingStatus;
import com.kfg.user.domain.ResidentialAddress;
import com.kfg.user.domain.UserOnboardingApplication;
import com.kfg.user.dto.SaveResidentialAddressCommand;
import com.kfg.user.dto.StartOnboardingCommand;
import com.kfg.user.dto.UpdateBasicDetailsCommand;
import com.kfg.user.dto.response.OnboardingApplicationSummary;
import com.kfg.user.dto.response.OnboardingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.kfg.user.exception.DuplicateResourceException;
import com.kfg.user.exception.OnboardingApplicationNotFoundException;
import com.kfg.user.mapper.OnboardingMapper;
import com.kfg.user.repository.UserOnboardingRepository;
import com.kfg.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserOnboardingServiceImpl implements UserOnboardingService {

    private final UserOnboardingRepository repository;
    private final UserRepository userRepository;
    private final OnboardingMapper mapper;

    @Override
    public Page<OnboardingApplicationSummary> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toSummary);
    }

    @Override
    @Transactional
    public OnboardingResponse start(StartOnboardingCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new DuplicateResourceException(
                    "Email is already registered to an existing account."
            );
        }
        if (userRepository.existsByPhoneNumber(command.phoneNumber())) {
            throw new DuplicateResourceException(
                    "Phone number is already registered to an existing account."
            );
        }
        List<UserOnboardingApplication> conflicts = repository.findActiveConflicts(
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
        return mapper.toResponse(repository.save(UserOnboardingApplication.start(basicDetails)));
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
        UserOnboardingApplication application = findOrThrow(applicationId);
        if (userRepository.existsByEmail(command.email())) {
            throw new DuplicateResourceException(
                    "Email is already registered to an existing account."
            );
        }
        if (userRepository.existsByPhoneNumber(command.phoneNumber())) {
            throw new DuplicateResourceException(
                    "Phone number is already registered to an existing account."
            );
        }
        List<UserOnboardingApplication> conflicts = repository.findOtherActiveApplicationsWithContactDetails(
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
        UserOnboardingApplication application = findOrThrow(applicationId);
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
        UserOnboardingApplication application = findOrThrow(applicationId);
        application.cancel(LocalDateTime.now());
        return mapper.toResponse(application);
    }

    private UserOnboardingApplication findOrThrow(UUID applicationId) {
        return repository.findById(applicationId)
                .orElseThrow(() -> new OnboardingApplicationNotFoundException(applicationId));
    }
}
