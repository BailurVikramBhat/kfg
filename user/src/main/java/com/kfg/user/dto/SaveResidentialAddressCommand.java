package com.kfg.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SaveResidentialAddressCommand(

        @NotBlank(message = "Address line 1 is required")
        @Size(max = 100, message = "Address line 1 cannot exceed 100 characters")
        String addressLine1,

        @Size(max = 100, message = "Address line 2 cannot exceed 100 characters")
        String addressLine2,

        @Size(max = 100, message = "Locality cannot exceed 100 characters")
        String locality,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City cannot exceed 100 characters")
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 100, message = "State cannot exceed 100 characters")
        String state,

        @NotBlank(message = "Postal code is required")
        @Size(max = 20, message = "Postal code cannot exceed 20 characters")
        String postalCode,

        @NotBlank(message = "Country code is required")
        @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be a 2-letter ISO code (e.g., IN, US)")
        String countryCode
) {
}
