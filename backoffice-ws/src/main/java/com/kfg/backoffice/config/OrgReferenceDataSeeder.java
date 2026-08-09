package com.kfg.backoffice.config;

import com.kfg.backoffice.domain.Department;
import com.kfg.backoffice.domain.Designation;
import com.kfg.backoffice.repository.DepartmentRepository;
import com.kfg.backoffice.repository.DesignationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrgReferenceDataSeeder implements ApplicationRunner {
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (departmentRepository.count() > 0) {
            return;
        }

        log.info("Seeding department and designation reference data");

        Map<String, Department> departments = departmentRepository.saveAll(List.of(
                Department.of("CUSTOMER_SUPPORT",        "Customer Support",         true),
                Department.of("KYC",                     "Know Your Customer",       true),
                Department.of("OPERATIONS",               "Operations",               true),
                Department.of("COMPLIANCE",               "Compliance",               true),
                Department.of("INTERNAL_AUDIT",           "Internal Audit",           true),
                Department.of("HUMAN_RESOURCES",          "Human Resources",          true),
                Department.of("INFORMATION_TECHNOLOGY",   "Information Technology",   true)
        )).stream().collect(Collectors.toMap(Department::getCode, d -> d));

        designationRepository.saveAll(List.of(
                Designation.of("KYC_OFFICER",                "KYC Officer",                departments.get("KYC"),                    true),
                Designation.of("KYC_MANAGER",                "KYC Manager",                departments.get("KYC"),                    true),
                Designation.of("HR_EXECUTIVE",               "HR Executive",               departments.get("HUMAN_RESOURCES"),        true),
                Designation.of("SYSTEM_ADMINISTRATOR",       "System Administrator",       departments.get("INFORMATION_TECHNOLOGY"), true),
                Designation.of("CUSTOMER_SUPPORT_EXECUTIVE", "Customer Support Executive", departments.get("CUSTOMER_SUPPORT"),       true)
        ));
    }
}
