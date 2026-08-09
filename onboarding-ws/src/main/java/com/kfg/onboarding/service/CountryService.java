package com.kfg.onboarding.service;

import com.kfg.onboarding.dto.response.CountryResponse;

import java.util.List;

public interface CountryService {

    List<CountryResponse> findAll();
}
