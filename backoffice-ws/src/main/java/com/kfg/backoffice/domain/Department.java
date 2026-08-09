package com.kfg.backoffice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="departments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department extends BaseEntity{
    @Id
    @Column(name = "code", length = 30, nullable = false, updatable = false)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    public static Department of(String code, String name, boolean enabled) {
        Department department = new Department();
        department.code = code;
        department.name = name;
        department.enabled = enabled;
        return department;
    }
}
