package com.kfg.backoffice.service.impl;

import com.kfg.backoffice.dto.response.DepartmentResponse;
import com.kfg.backoffice.repository.DepartmentRepository;
import com.kfg.backoffice.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl  implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override
    @Cacheable("departments")
    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAllByOrderByNameAsc().stream()
                .map(d -> new DepartmentResponse(d.getCode(),
                    d.isEnabled(), d.getName()
                ))
                .toList()
                ;
    }
}
