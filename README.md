# Kachiguda Financial Group — Backend

Backend services for the KFG neo-banking platform.

## Stack

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- PostgreSQL
- Maven multi-module

## Modules

| Module | Port | Description |
|--------|------|-------------|
| `onboarding-ws` | 8081 | Customer onboarding applications, KYC delegation, reference data |

## Prerequisites

- Java 25+
- PostgreSQL running on `localhost:5432`
- Database `onboarding_db` with user `onboarding_svc` / password `onboarding_pw`

## Running

```bash
cd onboarding-ws
./mvnw spring-boot:run
```

Schema is managed automatically via `ddl-auto: update` on startup.

## Health check

```
GET http://localhost:8081/actuator/health
```
