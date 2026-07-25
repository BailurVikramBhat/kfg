# ADR-004: Use Keycloak as the identity provider

## Status

Accepted

## Date

2026-07-25

## Context

KFG requires customer and employee authentication, MFA, access/refresh-token issuance, session management, password recovery, brute-force protection, administrative provisioning, and machine-to-machine authentication.

Implementing authentication credential storage and security protocol handling inside KFG business services would duplicate specialised identity-platform functionality and significantly increase security risk.

Customers and employees also have different security policies and administrative lifecycles.

## Decision

KFG will use **Keycloak** as its identity provider.

KFG will maintain separate customer and employee identity boundaries using separate Keycloak realms:

- `kfg-customers`
- `kfg-employees`

Keycloak owns:

- passwords and password hashing;
- TOTP/MFA credentials;
- login flows;
- token issuance;
- refresh/session lifecycle;
- brute-force protection;
- email verification and password-reset identity flows;
- OAuth2/OIDC client credentials for internal service identity.

KFG business services own:

- customer and employee business profiles;
- customer/account status;
- business permissions and separation-of-duties rules;
- banking authorisation decisions;
- audit records.

A lightweight Identity Adapter will isolate Keycloak administrative APIs used by KFG workflows such as provisioning, disabling identities, session termination, and controlled role synchronisation.

Customer browser applications will use Authorization Code Flow with PKCE. Internal service-to-service calls will use OAuth2 Client Credentials where synchronous authentication is required.

## Rationale

Keycloak provides mature standards-based authentication while allowing KFG to focus on banking authorisation and domain rules.

Separate realms make it possible to apply different session, MFA, password, and administrative policies to customers and employees and reduce accidental privilege crossover.

## Consequences

### Positive

- KFG never stores customer passwords or TOTP secrets.
- OIDC/OAuth2 standards are used instead of proprietary token mechanisms.
- MFA and session management are delegated to a specialised identity platform.
- Customer and employee authentication policies are cleanly separated.
- Service identities can be represented consistently.

### Negative / trade-offs

- Keycloak becomes critical infrastructure for login, refresh, and identity administration.
- Realm/client configuration must be versioned and reproducible.
- KFG must handle Keycloak outages and token-expiry semantics correctly.
- Business authorisation still requires application-level logic; Keycloak does not eliminate it.

### Operational implications

Realm configuration must be bootstrap-able from source-controlled configuration/scripts. Business services and the gateway validate tokens, and internal services must not assume network location equals trust.

## Alternatives considered

### Custom Spring Security identity service

Rejected because password storage, MFA recovery, session management, token issuance, and identity security are not KFG's differentiating business domain.

### Single Keycloak realm for customers and employees

Rejected because the populations require different security policies and stronger administrative separation.

### Cloud-only identity provider

Potentially valid for a real deployment, but less suitable for a self-contained learning platform and local development.

## Guardrails

- KFG databases must never store passwords or TOTP secrets.
- Business services must perform resource-level authorisation even after token validation.
- The frontend must not grant itself permissions through request payloads.
- User bearer tokens must not be blindly propagated to every downstream service.
- Keycloak administrative operations must be auditable.

## Related requirements

- [`§6 Identity and Authentication Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#6-identity-and-authentication-requirements)
- [`§38 Security Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#38-security-requirements)
- [`§27.2 Identity Adapter`](../requirements/KFG-V1-REQUIREMENTS.md#272-identity-adapter)

## Related ADRs

- [ADR-001: Use capability-oriented microservices for KFG V1](ADR-001-microservices-architecture.md)
