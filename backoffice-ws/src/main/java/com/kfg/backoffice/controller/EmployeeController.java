package com.kfg.backoffice.controller;

import com.kfg.backoffice.dto.command.CreateEmployeeCommand;
import com.kfg.backoffice.dto.response.EmployeeResponse;
import com.kfg.backoffice.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/backoffice/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    @PreAuthorize("hasAuthority('EMPLOYEE_CREATE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody @Valid CreateEmployeeCommand command) {
        EmployeeResponse response = employeeService.createEmployee(command);
        URI location = URI.create("/api/v1/backoffice/employees/" + response.employeeId());
        return ResponseEntity.created(location).body(response);
    }
}
