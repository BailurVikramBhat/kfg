package com.kfg.backoffice.service.impl;

import com.kfg.backoffice.domain.Designation;
import com.kfg.backoffice.domain.EmployeeProfile;
import com.kfg.backoffice.domain.EmployeeStatus;
import com.kfg.backoffice.dto.command.CreateEmployeeCommand;
import com.kfg.backoffice.dto.response.EmployeeResponse;
import com.kfg.backoffice.exception.DesignationNotFoundException;
import com.kfg.backoffice.exception.DuplicateResourceException;
import com.kfg.backoffice.repository.DesignationRepository;
import com.kfg.backoffice.repository.EmployeeProfileRepository;
import com.kfg.backoffice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeProfileRepository employeeProfileRepository;
    private final DesignationRepository designationRepository;

    @Override
    public EmployeeResponse createEmployee(CreateEmployeeCommand command) {
        if (employeeProfileRepository.existsByEmployeeNumber(command.employeeNumber())) {
            throw new DuplicateResourceException("Employee number already in use: " + command.employeeNumber());
        }
        if (employeeProfileRepository.existsByOfficialEmail(command.officialEmail())) {
            throw new DuplicateResourceException("Official email already in use: " + command.officialEmail());
        }
        String desCode = command.designationCode();
        Designation designation = designationRepository.findByCodeAndEnabledTrue(desCode)
                .orElseThrow(() -> new DesignationNotFoundException(desCode));
        EmployeeProfile employeeProfile = employeeProfileRepository.save(EmployeeProfile.builder()
                .employeeNumber(command.employeeNumber())
                .firstName(command.firstName())
                .lastName(command.lastName())
                .officialEmail(command.officialEmail())
                .status(EmployeeStatus.INVITED)
                .designation(designation)
                .department(designation.getDepartment())
                .build());
        return new EmployeeResponse(
                employeeProfile.getEmployeeId(),
                employeeProfile.getEmployeeNumber(),
                employeeProfile.getOfficialEmail(),
                employeeProfile.getFirstName(),
                employeeProfile.getLastName(),
                employeeProfile.getDesignation().getCode(),
                employeeProfile.getDesignation().getName(),
                employeeProfile.getDepartment().getCode(),
                employeeProfile.getDepartment().getName(),
                employeeProfile.getStatus()
        );
    }
}
