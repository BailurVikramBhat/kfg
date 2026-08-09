package com.kfg.backoffice.repository;

import com.kfg.backoffice.domain.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, UUID> {
    Optional<EmployeeProfile> findByKeycloakSubject(UUID keycloakSubject);
    boolean existsByEmployeeNumber(String employeeNumber);
    boolean existsByOfficialEmail(String officialEmail);
}
