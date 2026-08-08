package com.kfg.user.service;

import com.kfg.user.domain.OnboardingStatus;
import com.kfg.user.dto.response.AdminMetricsResponse;
import com.kfg.user.repository.CountryRepository;
import com.kfg.user.repository.UserOnboardingRepository;
import com.kfg.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserOnboardingRepository onboardingRepository;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    @Override
    public AdminMetricsResponse getMetrics() {
        Map<OnboardingStatus, Long> byStatus = onboardingRepository.countGroupedByStatus()
                .stream()
                .collect(Collectors.toMap(
                        UserOnboardingRepository.StatusCount::getStatus,
                        UserOnboardingRepository.StatusCount::getCount
                ));
        long totalApplications = byStatus.values().stream().mapToLong(Long::longValue).sum();

        long totalCountries = countryRepository.count();
        long enabledCountries = countryRepository.countByEnabled(true);

        return new AdminMetricsResponse(
                new AdminMetricsResponse.OnboardingMetrics(totalApplications, byStatus),
                userRepository.count(),
                new AdminMetricsResponse.CountryMetrics(totalCountries, enabledCountries, totalCountries - enabledCountries)
        );
    }
}
