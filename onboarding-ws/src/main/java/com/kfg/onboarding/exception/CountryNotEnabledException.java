package com.kfg.onboarding.exception;

public class CountryNotEnabledException extends RuntimeException {
    public CountryNotEnabledException(String message) {
        super(message);
    }
}
