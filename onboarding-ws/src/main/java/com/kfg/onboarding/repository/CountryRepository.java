package com.kfg.onboarding.repository;

import com.kfg.onboarding.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, String> {

    List<Country> findAllByOrderByNameAsc();

    long countByEnabled(boolean enabled);

    boolean existsByCodeAndEnabledTrue(String code);
}
