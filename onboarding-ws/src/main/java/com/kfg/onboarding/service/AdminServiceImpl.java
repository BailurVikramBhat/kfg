package com.kfg.onboarding.service;

import com.kfg.onboarding.domain.OnboardingStatus;
import com.kfg.onboarding.dto.response.AdminMetricsResponse;
import com.kfg.onboarding.repository.CountryRepository;
import com.kfg.onboarding.repository.OnboardingApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final OnboardingApplicationRepository onboardingRepository;
    private final CountryRepository countryRepository;

    @Override
    public AdminMetricsResponse getMetrics() {
        Map<OnboardingStatus, Long> byStatus = onboardingRepository.countGroupedByStatus()
                .stream()
                .collect(Collectors.toMap(
                        OnboardingApplicationRepository.StatusCount::getStatus,
                        OnboardingApplicationRepository.StatusCount::getCount
                ));
        long totalApplications = byStatus.values().stream().mapToLong(Long::longValue).sum();

        long totalCountries = countryRepository.count();
        long enabledCountries = countryRepository.countByEnabled(true);

        return new AdminMetricsResponse(
                new AdminMetricsResponse.OnboardingMetrics(totalApplications, byStatus),
                new AdminMetricsResponse.CountryMetrics(totalCountries, enabledCountries, totalCountries - enabledCountries)
        );
    }
}
