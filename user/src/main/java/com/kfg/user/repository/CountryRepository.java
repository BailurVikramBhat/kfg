package com.kfg.user.repository;

import com.kfg.user.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, String> {

    List<Country> findAllByOrderByNameAsc();

    long countByEnabled(boolean enabled);
}
