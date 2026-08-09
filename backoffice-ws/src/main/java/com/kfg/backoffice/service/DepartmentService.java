package com.kfg.backoffice.service;

import com.kfg.backoffice.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> findAll();
}
