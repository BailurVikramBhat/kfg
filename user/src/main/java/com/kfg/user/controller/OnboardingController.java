package com.kfg.user.controller;

import com.kfg.user.dto.SaveResidentialAddressCommand;
import com.kfg.user.dto.StartOnboardingCommand;
import com.kfg.user.dto.UpdateBasicDetailsCommand;
import com.kfg.user.dto.response.OnboardingApplicationSummary;
import com.kfg.user.dto.response.OnboardingResponse;
import com.kfg.user.service.UserOnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/onboarding-applications")
@RequiredArgsConstructor
public class OnboardingController {

    private final UserOnboardingService onboardingService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<OnboardingApplicationSummary>> findAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(onboardingService.findAll(pageable));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OnboardingResponse> start(@Valid @RequestBody StartOnboardingCommand command) {
        OnboardingResponse response = onboardingService.start(command);
        URI location = URI.create("/api/v1/onboarding-applications/" + response.applicationId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping(path = "/{applicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OnboardingResponse> getById(@PathVariable("applicationId") UUID applicationId) {
        return ResponseEntity.ok(onboardingService.getById(applicationId));
    }

    @PutMapping(
            path = "/{applicationId}/basic-details",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OnboardingResponse> updateBasicDetails(
            @PathVariable("applicationId") UUID applicationId,
            @Valid @RequestBody UpdateBasicDetailsCommand command
    ) {
        return ResponseEntity.ok(onboardingService.updateBasicDetails(applicationId, command));
    }

    @PutMapping(
            path = "/{applicationId}/residential-address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OnboardingResponse> saveResidentialAddress(
            @PathVariable("applicationId") UUID applicationId,
            @Valid @RequestBody SaveResidentialAddressCommand command
    ) {
        return ResponseEntity.ok(onboardingService.saveResidentialAddress(applicationId, command));
    }
}
