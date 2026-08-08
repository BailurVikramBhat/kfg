package com.kfg.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ResidentialAddress {

    @Column(name = "residential_address_line_1")
    private String addressLine1;

    @Column(name = "residential_address_line_2")
    private String addressLine2;

    @Column(name = "residential_locality")
    private String locality;

    @Column(name = "residential_city")
    private String city;

    @Column(name = "residential_state")
    private String state;

    @Column(name = "residential_postal_code")
    private String postalCode;

    @Column(name = "residential_country_code")
    private String countryCode;
}
