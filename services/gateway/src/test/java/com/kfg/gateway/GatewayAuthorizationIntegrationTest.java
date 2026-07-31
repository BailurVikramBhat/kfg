package com.kfg.gateway;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class GatewayAuthorizationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void healthIsPublic() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void protectedEndpointWithoutTokenReturnsUnauthorized() {
        webTestClient.get()
                .uri("/api/customer/ping")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void customerCanAccessCustomerEndpoint() {
        webTestClient
                .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .get()
                .uri("/api/customer/ping")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.audience").isEqualTo("customer");
    }

    @Test
    void customerCannotAccessEmployeeEndpoint() {
        webTestClient
                .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .get()
                .uri("/api/employee/ping")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void employeeCanAccessEmployeeEndpoint() {
        webTestClient
                .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
                .get()
                .uri("/api/employee/ping")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.audience").isEqualTo("employee");
    }
}
