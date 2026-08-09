package com.kfg.onboarding.service;

import com.kfg.onboarding.dto.response.CountryResponse;
import com.kfg.onboarding.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    @Cacheable("countries")
    public List<CountryResponse> findAll() {
        return countryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(c -> new CountryResponse(c.getCode(), c.getName(), c.isEnabled()))
                .toList();
    }
}
