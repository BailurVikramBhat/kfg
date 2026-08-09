package com.kfg.backoffice.repository;

import com.kfg.backoffice.domain.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DesignationRepository extends JpaRepository<Designation, String> {
    List<Designation> findAllByOrderByNameAsc();
    Optional<Designation> findByCodeAndEnabledTrue(String code);
}
