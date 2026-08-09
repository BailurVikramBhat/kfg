package com.kfg.backoffice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="designations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Designation extends BaseEntity{
    @Id
    @Column(name = "code", length = 40, nullable = false, updatable = false)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_code", referencedColumnName = "code", nullable = false)
    private Department department;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    public static Designation of(String code, String name, Department department, boolean enabled) {
        Designation designation = new Designation();
        designation.code = code;
        designation.name = name;
        designation.department = department;
        designation.enabled = enabled;
        return designation;
    }
}
