package com.kfg.backoffice.service.impl;

import com.kfg.backoffice.dto.response.DesignationResponse;
import com.kfg.backoffice.repository.DesignationRepository;
import com.kfg.backoffice.service.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DesignationServiceImpl implements DesignationService {
    private final DesignationRepository designationRepository;
    @Override
    @Cacheable("designations")
    public List<DesignationResponse> findAll() {
        return designationRepository.findAllByOrderByNameAsc().stream()
                .map(d -> new DesignationResponse(d.getCode(),
                        d.isEnabled(), d.getName()
                        ))
                .toList()
                ;
    }
}
