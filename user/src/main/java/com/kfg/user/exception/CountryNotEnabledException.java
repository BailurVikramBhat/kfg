package com.kfg.user.exception;

public class CountryNotEnabledException extends RuntimeException {
    public CountryNotEnabledException(String message) {
        super(message);
    }
}
