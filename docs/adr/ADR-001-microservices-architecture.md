# ADR-001: Use capability-oriented microservices for KFG V1

## Status

Accepted

## Date

2026-07-25

## Context

KFG V1 is intended to model a production-grade digital neo-bank rather than a single CRUD application. The platform contains materially different business capabilities: customer onboarding, KYC, account metadata, ledger accounting, beneficiaries, payments, notifications, back-office workflows, audit, and statement generation.

These capabilities have different correctness requirements, failure modes, security boundaries, data-retention needs, and scaling characteristics. The project also explicitly aims to exercise service ownership, asynchronous integration, resilience, independent deployment, observability, and contract testing.

At the same time, KFG is initially developed by one engineer on a 16 GB development machine. Splitting every technical concern into an independent service would create a distributed monolith and excessive operational overhead.

## Decision

KFG V1 will use **capability-oriented, independently deployable Spring Boot microservices**. Service boundaries will follow meaningful business responsibilities, not arbitrary technical layers.

The initial service set is:

- API Gateway
- Identity Adapter
- Customer Service
- KYC Service
- Account Service
- Ledger Service
- Beneficiary Service
- Payment Service
- Notification Service
- Backoffice Service
- Audit Service
- Statement Service

The IMPS Network Simulator is an independent simulator representing an external system, not a KFG business service.

Capabilities that do not yet justify independent deployment remain inside the owning service. For example, IMPS reconciliation remains part of Payment Service in V1, and employee/case/approval workflows remain inside Backoffice Service.

## Rationale

This design gives KFG meaningful service autonomy where it matters while resisting unnecessary fragmentation.

It supports:

- strict financial ownership in Ledger Service;
- independent KYC and identity security boundaries;
- isolation of external-payment orchestration from accounting;
- asynchronous notification and audit processing;
- separate customer-facing and back-office operational concerns;
- independent test and deployment boundaries.

The architecture is intentionally smaller than an enterprise bank's production estate because V1 values correctness and comprehensibility over service count.

## Consequences

### Positive

- Business ownership is explicit.
- Deployments can evolve independently.
- Failures can be isolated by capability.
- Data ownership can be enforced per service.
- Kafka and contract-testing patterns are exercised meaningfully.
- Services can be developed and tested in slices rather than running the entire platform locally.

### Negative / trade-offs

- Distributed calls introduce latency and partial-failure concerns.
- Cross-service workflows require explicit consistency strategies.
- Local development needs containerised infrastructure and selective service startup.
- Debugging distributed flows is harder than debugging a modular monolith.
- More deployment artefacts, configuration, contracts, and observability are required.

### Operational implications

KFG must provide correlation IDs, distributed tracing, health checks, service-to-service authentication, timeout policies, contract tests, and reliable event publication.

## Alternatives considered

### Modular monolith

A modular monolith would reduce operational complexity and would be a valid architecture for many real systems. It was not selected because KFG explicitly aims to learn and demonstrate production microservice concerns such as independent service data ownership, Kafka integration, distributed resilience, contract testing, and deployment.

### Fine-grained microservices

Splitting approvals, reconciliation, cases, employee management, idempotency, or interest processing into separate services was rejected for V1. It would increase network boundaries and deployment overhead without corresponding business autonomy.

### Shared-services architecture

A central database service or generic business service was rejected because it would blur ownership and create coupling between unrelated domains.

## Guardrails

- A new microservice requires a clear business ownership or operational-isolation reason.
- Microservices must not be introduced solely because a framework or database table exists.
- Services must not share domain entities or directly query another service's database.
- A synchronous dependency should exist only when the caller requires an immediate answer.
- KFG must remain runnable locally in focused slices.

## Related requirements

- [`§3 V1 Scope Summary`](../requirements/KFG-V1-REQUIREMENTS.md#3-v1-scope-summary)
- [`§26 Service Architecture`](../requirements/KFG-V1-REQUIREMENTS.md#26-service-architecture)
- [`§27 Service Ownership`](../requirements/KFG-V1-REQUIREMENTS.md#27-service-ownership)
- [`§34 Synchronous Integration Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#34-synchronous-integration-requirements)
- [`§39 Reliability and Resilience Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#39-reliability-and-resilience-requirements)

## Related ADRs

- [ADR-002: Use a monorepo for KFG V1](ADR-002-monorepo.md)
- [ADR-003: Enforce database ownership per service](ADR-003-database-per-service-ownership.md)
