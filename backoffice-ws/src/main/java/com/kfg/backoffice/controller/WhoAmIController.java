package com.kfg.backoffice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WhoAmIController {
    @GetMapping("/api/v1/backoffice/whoami")
    public ResponseEntity<?> whoAmI(JwtAuthenticationToken authentication) {
        List<String> permissions = authentication.getAuthorities().stream()
                .filter(a -> !(a instanceof FactorGrantedAuthority))
                .map(GrantedAuthority::getAuthority)
                .toList();
        return ResponseEntity.ok(new WhoAmIResponse(
                authentication.getToken().getSubject(),
                authentication.getToken().getClaimAsString("preferred_username"),
                permissions
                ));
    }
    private record WhoAmIResponse(String subject, String username, List<String> permissions) {}

}
