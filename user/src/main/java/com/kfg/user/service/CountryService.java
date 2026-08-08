package com.kfg.user.service;

import com.kfg.user.dto.response.CountryResponse;

import java.util.List;

public interface CountryService {

    List<CountryResponse> findAll();
}
