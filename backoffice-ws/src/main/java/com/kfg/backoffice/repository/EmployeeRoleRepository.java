package com.kfg.backoffice.repository;

import com.kfg.backoffice.domain.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRoleRepository extends JpaRepository<EmployeeRole, UUID> {

}
