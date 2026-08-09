package com.kfg.backoffice.controller;

import com.kfg.backoffice.config.SecurityConfig;
import com.kfg.backoffice.repository.EmployeeProfileRepository;
import com.kfg.backoffice.security.KeycloakAuthoritiesConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WhoAmIController.class)
@Import({SecurityConfig.class, KeycloakAuthoritiesConverter.class, WhoAmIControllerTest.NoOpJwtDecoderConfig.class})
class WhoAmIControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockitoBean
    EmployeeProfileRepository employeeProfileRepository;

    @Test
    void noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/backoffice/whoami"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void withAuthority_returns200WithAuthorities() throws Exception {
        mockMvc.perform(get("/api/v1/backoffice/whoami")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ONBOARDING_APPLICATION_READ"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorities[0]").value("ONBOARDING_APPLICATION_READ"));
    }

    @TestConfiguration
    static class NoOpJwtDecoderConfig {
        @Bean
        JwtDecoder jwtDecoder() {
            return token -> { throw new UnsupportedOperationException("not used in these tests"); };
        }
    }
}