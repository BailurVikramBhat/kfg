package com.kfg.gateway.security;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kfg.security")
public record KfgSecurityProperties(URI customerIssuer, URI employeeIssuer) {
}
