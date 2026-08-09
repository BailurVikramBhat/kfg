package com.kfg.backoffice.dto.response;

import com.kfg.backoffice.domain.Department;
import com.kfg.backoffice.domain.Designation;
import com.kfg.backoffice.domain.EmployeeStatus;

import java.util.UUID;

public record EmployeeResponse(
        UUID employeeId, String employeeNumber, String officialEmail,
        String firstName, String lastName,
        String designationCode, String designationName, String departmentCode, String departmentName, EmployeeStatus status
) {}
