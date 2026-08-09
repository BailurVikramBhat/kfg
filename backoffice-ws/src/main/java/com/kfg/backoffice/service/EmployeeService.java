package com.kfg.backoffice.service;

import com.kfg.backoffice.dto.command.CreateEmployeeCommand;
import com.kfg.backoffice.dto.response.EmployeeResponse;

public interface EmployeeService {
    EmployeeResponse createEmployee(CreateEmployeeCommand command);
}
