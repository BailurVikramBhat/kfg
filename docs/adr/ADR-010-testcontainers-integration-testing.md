# ADR-010: Use Testcontainers with production-like dependencies for integration testing

## Status

Accepted

## Date

2026-07-25

## Context

KFG relies on database locking, PostgreSQL transaction semantics, Kafka delivery, Redis behaviour, Keycloak-issued tokens, MinIO object storage, Flyway migrations, and external-service failure handling.

Substituting materially different in-memory technologies can produce fast tests that validate behaviour KFG will never run in production. This is especially dangerous for Ledger Service, where locking, isolation, constraints, and concurrency determine financial correctness.

The development machine has 16 GB RAM, so the strategy must provide production-like fidelity without requiring the entire distributed platform to run for every test.

## Decision

KFG integration tests will use **Testcontainers** for production-like infrastructure where the dependency materially affects behaviour.

Primary containers include:

- PostgreSQL;
- Kafka;
- Redis where Redis-specific behaviour matters;
- Keycloak for real authentication/authorisation integration tests;
- MinIO for object-storage integration tests;
- Toxiproxy or equivalent when controlled network faults are required.

KFG will **not use H2 as a substitute for PostgreSQL financial persistence tests**.

Testing will be layered:

- pure domain tests run without Spring/containers;
- repository/component tests start only required containers;
- Spring Boot integration tests use the smallest realistic dependency set;
- distributed E2E tests run broader infrastructure;
- performance, chaos, DAST, and soak testing run in dedicated/scheduled environments.

Flyway migrations must be exercised against clean PostgreSQL databases in integration testing.

External systems such as the IMPS network may be represented by WireMock for component tests and by the dedicated IMPS simulator for distributed E2E tests.

## Rationale

The approach maximises correctness where infrastructure semantics matter while preserving fast feedback for pure business logic.

It also makes tests reproducible in CI: `mvn verify` must not depend on a developer manually starting a local PostgreSQL/Kafka instance.

## Consequences

### Positive

- Tests exercise real PostgreSQL locking, SQL, constraints, and transactions.
- Kafka serialisation/consumer behaviour is tested against a real broker.
- Security tests can use genuine Keycloak tokens and realm configuration.
- CI and developer tests use repeatable isolated infrastructure.
- Production-like schema migrations are continuously validated.

### Negative / trade-offs

- Integration tests are slower than in-memory substitutes.
- Docker is required for many test suites.
- Container startup consumes memory and CPU.
- Test architecture must avoid starting unnecessary infrastructure per test class.
- Parallel tests require careful isolation and resource management.

### Operational implications

The local test strategy must be resource-conscious on the i5-13420H/16 GB machine. Heavy distributed suites should run in CI/scheduled environments rather than forcing every developer invocation to start the whole bank.

## Alternatives considered

### H2 for repository/integration tests

Rejected for financial persistence because H2 does not faithfully reproduce PostgreSQL locking, isolation, SQL, constraint, JSON, and concurrency behaviour.

### Shared manually started Docker infrastructure

Useful for manual development, but rejected as the default automated-test dependency because it makes tests environment-dependent and difficult to reproduce in CI.

### Mock every infrastructure dependency

Rejected because verifying that `KafkaTemplate.send()` or a mocked repository method was called does not prove real serialisation, migrations, locking, SQL, or transaction behaviour.

### Full platform for every integration test

Rejected because it would be slow, fragile, and wasteful. Tests should run the smallest realistic dependency set required by the behaviour under test.

## Guardrails

- Pure domain logic must remain testable without Spring or containers where practical.
- PostgreSQL-specific financial behaviour must be tested on PostgreSQL.
- Integration tests must not depend on pre-existing local data.
- Tests must generate isolated data and avoid shared mutable fixtures.
- Sleep-based async tests are prohibited; use deterministic clocks/Awaitility where appropriate.
- Container/resource reuse is an optimisation, never a correctness dependency.
- CI starts from clean infrastructure.

## Related requirements

- [`§41 Testing Strategy`](../requirements/KFG-V1-REQUIREMENTS.md#41-testing-strategy)
- [`§41.5 Database testing`](../requirements/KFG-V1-REQUIREMENTS.md#415-database-testing)
- [`§41.6 Spring integration tests`](../requirements/KFG-V1-REQUIREMENTS.md#416-spring-integration-tests)
- [`§41.9 Kafka integration tests`](../requirements/KFG-V1-REQUIREMENTS.md#419-kafka-integration-tests)
- [`§41.12 Keycloak/security tests`](../requirements/KFG-V1-REQUIREMENTS.md#4112-keycloaksecurity-tests)

## Related ADRs

- [ADR-003: Enforce database ownership per service](ADR-003-database-per-service-ownership.md)
- [ADR-006: Use Kafka with Avro for asynchronous integration](ADR-006-kafka-avro-eventing.md)
- [ADR-008: Make KFG-to-KFG transfers atomic inside Ledger Service](ADR-008-internal-transfer-atomicity.md)
