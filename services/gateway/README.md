# KFG API Gateway

The gateway is KFG's public API entry point and JWT authentication boundary.

## Baseline

- Java 25
- Spring Boot 4.1.0
- Spring Cloud 2025.1.2
- Spring Cloud Gateway Server WebFlux
- OAuth2 Resource Server with Keycloak JWTs

The gateway trusts only the configured customer and employee Keycloak issuers.
Keycloak realm roles are converted to Spring Security authorities with the
`ROLE_` prefix.

## Run

From the repository root:

```powershell
.\mvnw.cmd -pl services/gateway -am spring-boot:run
```

Environment variables:

| Variable | Default |
|---|---|
| `KFG_GATEWAY_PORT` | `8080` |
| `KFG_CUSTOMER_ISSUER` | `http://localhost:8081/realms/kfg-customers` |
| `KFG_EMPLOYEE_ISSUER` | `http://localhost:8081/realms/kfg-employees` |

## Verification endpoints

The two ping endpoints are temporary probes for validating authentication and
authorization before downstream services are connected.

| Request | Expected result |
|---|---|
| `GET /actuator/health` without a token | `200 OK` |
| `GET /api/customer/ping` without a token | `401 Unauthorized` |
| Customer token with realm role `CUSTOMER` | Customer endpoint returns `200 OK` |
| Customer token calling employee endpoint | `403 Forbidden` |
| Employee token with realm role `EMPLOYEE` | Employee endpoint returns `200 OK` |

Run the automated checks from the repository root:

```powershell
.\mvnw.cmd -pl services/gateway -am verify
```
