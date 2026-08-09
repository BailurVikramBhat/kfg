package com.kfg.backoffice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String CLIENT_ID = "kfg-backoffice";


    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> resourceAccess = source.getClaimAsMap("resource_access");
        if(resourceAccess== null || !(resourceAccess.get(CLIENT_ID) instanceof Map<?,?> clientAccess)) {
            return List.of();
        }
        if(!(clientAccess.get("roles") instanceof List<?> roles)) {
            return List.of();
        }
        return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority((String) role))
                .toList();
    }
}
