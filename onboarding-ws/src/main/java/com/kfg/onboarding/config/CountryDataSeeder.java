package com.kfg.onboarding.config;

import com.kfg.onboarding.domain.Country;
import com.kfg.onboarding.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CountryDataSeeder implements ApplicationRunner {

    private final CountryRepository countryRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (countryRepository.count() > 0) {
            return;
        }

        log.info("Seeding country reference data");

        countryRepository.saveAll(List.of(
                Country.of("AU", "Australia",            false),
                Country.of("AE", "United Arab Emirates", false),
                Country.of("CA", "Canada",               false),
                Country.of("DE", "Germany",              false),
                Country.of("FR", "France",               false),
                Country.of("GB", "United Kingdom",       false),
                Country.of("IN", "India",                true),
                Country.of("JP", "Japan",                false),
                Country.of("MY", "Malaysia",             false),
                Country.of("NL", "Netherlands",          false),
                Country.of("NZ", "New Zealand",          false),
                Country.of("QA", "Qatar",                false),
                Country.of("SA", "Saudi Arabia",         false),
                Country.of("SG", "Singapore",            false),
                Country.of("US", "United States",        false)
        ));
    }
}
