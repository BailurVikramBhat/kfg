package com.kfg.user.dto.response;

public record ResidentialAddressResponse(
        String addressLine1,
        String addressLine2,
        String locality,
        String city,
        String state,
        String postalCode,
        String countryCode
) {
}
