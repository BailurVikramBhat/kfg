package com.kfg.gateway.security;

import java.net.URI;
import java.util.Map;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerReactiveAuthenticationManagerResolver;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(KfgSecurityProperties.class)
class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .pathMatchers("/api/customer/**").hasRole("CUSTOMER")
                        .pathMatchers("/api/employee/**").hasRole("EMPLOYEE")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 ->
                        oauth2.authenticationManagerResolver(authenticationManagerResolver))
                .build();
    }

    @Bean
    ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver(
            KfgSecurityProperties properties) {

        Map<String, ReactiveAuthenticationManager> managers = Map.of(
                properties.customerIssuer().toString(), authenticationManager(properties.customerIssuer()),
                properties.employeeIssuer().toString(), authenticationManager(properties.employeeIssuer()));

        return new JwtIssuerReactiveAuthenticationManagerResolver(
                issuer -> Mono.justOrEmpty(managers.get(issuer)));
    }

    private ReactiveAuthenticationManager authenticationManager(URI issuer) {
        String issuerUri = issuer.toString();
        String jwkSetUri = issuerUri + "/protocol/openid-connect/certs";

        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuerUri));

        JwtReactiveAuthenticationManager manager = new JwtReactiveAuthenticationManager(decoder);
        manager.setJwtAuthenticationConverter(new KeycloakRealmRoleConverter());
        return manager;
    }
}
