package com.kfg.backoffice.security;

import com.kfg.backoffice.domain.EmployeeStatus;
import com.kfg.backoffice.repository.EmployeeProfileRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmployeeContextFilter extends OncePerRequestFilter {
    private final EmployeeProfileRepository employeeProfileRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthenticationToken jwtAuth) {
            UUID subject = UUID.fromString(Objects.requireNonNull(jwtAuth.getToken().getSubject()));
            boolean active = employeeProfileRepository.findByKeycloakSubject(subject)
                    .map(profile -> profile.getStatus() == EmployeeStatus.ACTIVE)
                    .orElse(false);
            if(!active) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, response);

    }
}
