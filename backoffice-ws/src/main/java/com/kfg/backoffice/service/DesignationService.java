package com.kfg.backoffice.service;

import com.kfg.backoffice.dto.response.DesignationResponse;

import java.util.List;

public interface DesignationService {
    List<DesignationResponse> findAll();
}
