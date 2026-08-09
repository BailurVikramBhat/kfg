package com.kfg.onboarding.dto;

import jakarta.validation.constraints.*;

public record StartOnboardingCommand(

        @NotBlank(message = "First name is required")
        @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
        @Pattern(regexp = "^[\\p{L}\\s.'\\-]+$", message = "First name contains invalid characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        @Pattern(regexp = "^[\\p{L}\\s.'\\-]+$", message = "Last name contains invalid characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        @Size(max = 254, message = "Email cannot exceed 254 characters")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format (e.g., +919876543210)")
        String phoneNumber
) {
}
