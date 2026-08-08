package com.kfg.user.controller;

import com.kfg.user.domain.KycType;
import com.kfg.user.dto.response.CountryResponse;
import com.kfg.user.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reference")
@RequiredArgsConstructor
public class ReferenceController {

    private final CountryService countryService;

    private record KycTypeOption(String value, String label) {}

    private static final List<KycTypeOption> KYC_TYPES = List.of(
            new KycTypeOption(KycType.STANDARD.name(), "Standard KYC"),
            new KycTypeOption(KycType.ENHANCED.name(), "Enhanced KYC")
    );

    @GetMapping(path = "/kyc-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<KycTypeOption>> getKycTypes() {
        return ResponseEntity.ok(KYC_TYPES);
    }

    @GetMapping(path = "/countries", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CountryResponse>> getCountries() {
        return ResponseEntity.ok(countryService.findAll());
    }
}
