package com.kfg.gateway.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import reactor.test.StepVerifier;

class KeycloakRealmRoleConverterTest {

    private final KeycloakRealmRoleConverter converter = new KeycloakRealmRoleConverter();

    @Test
    void convertsKeycloakRealmRolesToSpringAuthorities() {
        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "customer-1",
                        "preferred_username", "customer@example.com",
                        "scope", "openid profile",
                        "realm_access", Map.of("roles", List.of("customer"))));

        StepVerifier.create(converter.convert(jwt))
                .assertNext(authentication -> {
                    assertThat(authentication.getName()).isEqualTo("customer@example.com");
                    assertThat(authentication.getAuthorities())
                            .extracting("authority")
                            .contains("ROLE_CUSTOMER", "SCOPE_openid", "SCOPE_profile");
                })
                .verifyComplete();
    }
}
