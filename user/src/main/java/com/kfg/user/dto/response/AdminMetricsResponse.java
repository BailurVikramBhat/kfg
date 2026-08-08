package com.kfg.user.dto.response;

import com.kfg.user.domain.OnboardingStatus;

import java.util.Map;

public record AdminMetricsResponse(
        OnboardingMetrics onboardingApplications,
        long registeredUsers,
        CountryMetrics countries
) {
    public record OnboardingMetrics(long total, Map<OnboardingStatus, Long> byStatus) {}

    public record CountryMetrics(long total, long enabled, long disabled) {}
}
