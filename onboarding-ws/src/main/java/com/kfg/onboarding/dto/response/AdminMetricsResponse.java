package com.kfg.onboarding.dto.response;

import com.kfg.onboarding.domain.OnboardingStatus;

import java.util.Map;

public record AdminMetricsResponse(
        OnboardingMetrics onboardingApplications,
        CountryMetrics countries
) {
    public record OnboardingMetrics(long total, Map<OnboardingStatus, Long> byStatus) {}

    public record CountryMetrics(long total, long enabled, long disabled) {}
}
