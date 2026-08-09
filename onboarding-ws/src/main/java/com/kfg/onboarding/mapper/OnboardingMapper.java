package com.kfg.onboarding.mapper;

import com.kfg.onboarding.domain.*;
import com.kfg.onboarding.dto.response.*;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OnboardingMapper {

    public OnboardingApplicationSummary toSummary(OnboardingApplication application) {
        BasicDetails details = application.getBasicDetails();
        String name = details != null
                ? (details.getFirstName() + " " + details.getLastName()).trim()
                : null;
        return new OnboardingApplicationSummary(
                application.getId(),
                application.getStatus(),
                resolveNextStep(application.getStatus()),
                name,
                details != null ? details.getEmail() : null,
                details != null ? details.getPhoneNumber() : null,
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }

    public OnboardingResponse toResponse(OnboardingApplication application) {
        return new OnboardingResponse(
                application.getId(),
                application.getStatus(),
                resolveNextStep(application.getStatus()),
                resolveAllowedActions(application.getStatus()),
                toBasicDetailsResponse(application.getBasicDetails()),
                toResidentialAddressResponse(application.getResidentialAddress()),
                null,
                application.getVersion(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }

    private NextOnboardingStep resolveNextStep(OnboardingStatus status) {
        return switch (status) {
            case DRAFT -> NextOnboardingStep.ADDRESS;
            case READY_FOR_KYC -> NextOnboardingStep.KYC;
            case KYC_IN_PROGRESS -> NextOnboardingStep.KYC_REVIEW;
            default -> NextOnboardingStep.NONE;
        };
    }

    private Set<OnboardingAction> resolveAllowedActions(OnboardingStatus status) {
        return switch (status) {
            case DRAFT -> Set.of(
                    OnboardingAction.UPDATE_BASIC_DETAILS,
                    OnboardingAction.SAVE_RESIDENTIAL_ADDRESS,
                    OnboardingAction.CANCEL
            );
            case READY_FOR_KYC -> Set.of(
                    OnboardingAction.UPDATE_BASIC_DETAILS,
                    OnboardingAction.SAVE_RESIDENTIAL_ADDRESS,
                    OnboardingAction.START_KYC,
                    OnboardingAction.CANCEL
            );
            case KYC_IN_PROGRESS -> Set.of(OnboardingAction.RECORD_KYC_DECISION);
            default -> Set.of();
        };
    }

    private BasicDetailsResponse toBasicDetailsResponse(BasicDetails details) {
        if (details == null) return null;
        return new BasicDetailsResponse(
                details.getFirstName(),
                details.getLastName(),
                details.getEmail(),
                details.getPhoneNumber()
        );
    }

    private ResidentialAddressResponse toResidentialAddressResponse(ResidentialAddress address) {
        if (address == null) return null;
        return new ResidentialAddressResponse(
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getLocality(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountryCode()
        );
    }
}
