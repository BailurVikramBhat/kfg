# ADR-003: Enforce database ownership per service

## Status

Accepted

## Date

2026-07-25

## Context

KFG's microservices represent different business capabilities and must be able to evolve without hidden persistence coupling. Direct cross-service SQL joins would bypass service APIs, authorisation, business invariants, audit behaviour, and future independent deployment choices.

Running a separate physical PostgreSQL server for every service on the local 16 GB development machine would be wasteful. The architecture therefore needs to distinguish **logical ownership** from **physical hosting**.

## Decision

Each KFG service will exclusively own its persistent data.

For local development, KFG may run **one PostgreSQL cluster with separate logical databases and credentials per service**. In higher environments, those databases may be moved to separate PostgreSQL instances without changing ownership semantics.

Examples include:

- `customer_db`
- `kyc_db`
- `account_db`
- `ledger_db`
- `payment_db`
- `beneficiary_db`
- `backoffice_db`
- `notification_db`
- `audit_db`
- `statement_db`

Rules:

1. A service may read and write only its own database.
2. A service must not create SQL joins, foreign keys, views, or stored procedures that reach into another service's database.
3. Cross-service references are stored as identifiers without cross-database foreign keys.
4. Cross-service data is obtained through supported APIs, events, or purpose-built read models.
5. Each service owns its Flyway migrations.
6. Runtime database users receive least-privilege access only to the owning database.

## Rationale

The decision preserves real microservice ownership while allowing resource-efficient local development.

It prevents a service from bypassing another service's invariants. This is particularly important for Ledger Service, where no other service may update journals or balances directly.

## Consequences

### Positive

- Clear ownership and schema evolution.
- Services can be moved to independent database infrastructure later.
- Business rules cannot be bypassed with convenient cross-service SQL.
- Database credentials and privileges can be isolated.
- Failure and scaling characteristics can differ by domain.

### Negative / trade-offs

- Cross-service reporting cannot use simple relational joins.
- Some views require API composition or asynchronous projections.
- Eventual consistency must be accepted for selected read models.
- Duplicate identifiers or selected snapshots may exist across service boundaries.

### Operational implications

Local bootstrap scripts must create logical databases and users, but application schemas are created only through each service's Flyway migrations.

## Alternatives considered

### One shared KFG database/schema

Rejected because it would create tight persistence coupling, enable accidental cross-domain writes, and make service ownership largely cosmetic.

### One physical PostgreSQL instance per service locally

Architecturally valid, but rejected for ordinary local development because it wastes memory and operational effort on the user's 16 GB machine.

### Shared read-only reporting database queries

Direct read access to operational service databases was rejected. Reporting/read models should be built explicitly from APIs or events.

## Guardrails

- No cross-service foreign keys.
- No service receives another service's database credentials.
- No production workflow depends on DBeaver/manual cross-database SQL.
- Flyway is the only supported schema-evolution mechanism for service-owned tables.
- Hibernate schema generation must not create production schemas.

## Related requirements

- [`§28 Database Ownership`](../requirements/KFG-V1-REQUIREMENTS.md#28-database-ownership)
- [`§29 Data Architecture Standards`](../requirements/KFG-V1-REQUIREMENTS.md#29-data-architecture-standards)
- [`§37.11 JPA`](../requirements/KFG-V1-REQUIREMENTS.md#3711-jpa)
- [`§38.13 Database credentials`](../requirements/KFG-V1-REQUIREMENTS.md#3813-database-credentials)

## Related ADRs

- [ADR-001: Use capability-oriented microservices for KFG V1](ADR-001-microservices-architecture.md)
- [ADR-005: Ledger Service owns financial balances](ADR-005-ledger-owns-financial-balances.md)
