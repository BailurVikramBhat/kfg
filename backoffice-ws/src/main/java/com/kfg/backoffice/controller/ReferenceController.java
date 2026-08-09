package com.kfg.backoffice.controller;

import com.kfg.backoffice.dto.response.DepartmentResponse;
import com.kfg.backoffice.dto.response.DesignationResponse;
import com.kfg.backoffice.service.DepartmentService;
import com.kfg.backoffice.service.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/backoffice/reference")
public class ReferenceController {
    private final DesignationService designationService;
    private final DepartmentService departmentService;

    @GetMapping(path = "/departments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DepartmentResponse>> findAllDepartments() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping(path = "/designations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DesignationResponse>> findAllDesignations() {
        return ResponseEntity.ok(designationService.findAll());
    }


}
