package com.kfg.backoffice.dto.command;

import com.kfg.backoffice.domain.Designation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateEmployeeCommand(
        @NotBlank String employeeNumber,
        @NotBlank @Email String officialEmail,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotEmpty String designationCode
        ) {
}
