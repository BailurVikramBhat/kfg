# ADR-002: Use a monorepo for KFG V1

## Status

Accepted

## Date

2026-07-25

## Context

KFG V1 contains multiple independently deployable services, shared API/event contracts, infrastructure configuration, end-to-end tests, architecture documentation, and operational scripts.

The project is initially developed by a single engineer. A multi-repository model would require repeated repository setup, duplicated CI/CD configuration, coordinated changes across repositories, and more administration without providing meaningful team-isolation benefits.

The repository structure still needs to preserve service boundaries and independent build/deployment artefacts.

## Decision

All KFG V1 backend services, simulators, platform configuration, contracts, cross-service tests, scripts, requirements, ADRs, and runbooks will live in a **single Git monorepo** named `kachiguda-financial-group`.

The monorepo may use a root Maven parent for dependency and plugin alignment, but each deployable service will retain:

- its own Maven module;
- its own Spring Boot application;
- its own Dockerfile;
- its own Flyway migrations;
- its own database ownership;
- its own configuration;
- its own tests;
- its own deployable image.

A monorepo does not grant services permission to import each other's domain internals or persistence entities.

## Rationale

The monorepo optimises for the actual KFG team size and workflow.

It enables:

- atomic changes to a producer and its contracts;
- one place for architecture documentation;
- consistent Java, Spring Boot, Maven, testing, and formatting standards;
- path-aware CI while retaining a full-platform build;
- simpler local setup;
- easier refactoring before V1 stabilises.

It also aligns well with the project's Kanban delivery model: a vertical slice can modify several modules and contracts in one reviewed change.

## Consequences

### Positive

- One clone provides the whole backend platform.
- Cross-service changes can be reviewed atomically.
- Dependency versions can be aligned centrally.
- CI/CD conventions are easier to standardise.
- Requirements, ADRs, service code, and contracts evolve together.

### Negative / trade-offs

- Repository size grows over time.
- CI needs path-aware optimisation to avoid rebuilding everything unnecessarily.
- Poor discipline could accidentally turn the monorepo into a shared-code distributed monolith.
- Access control cannot be separated per repository without additional tooling.

### Operational implications

CI should build impacted modules for ordinary pull requests and periodically run a full-platform build. Service images remain separately versioned and deployable.

## Alternatives considered

### Repository per service

Rejected for V1 because it adds administrative overhead and makes coordinated development unnecessarily expensive for a single developer.

### One repository and one deployable application

Rejected because repository topology and runtime architecture are separate concerns. KFG still requires independent service deployments and data ownership.

### Multiple repositories grouped by domain

This could become reasonable if separate teams own distinct domains later, but it adds no current benefit.

## Guardrails

- No service may depend directly on another service's JPA entity or repository module.
- Shared code must be deliberately small and infrastructure-oriented.
- A root build may orchestrate services but must not merge them into one runtime.
- Cross-service contracts must remain explicit.

## Related requirements

- [`§26 Service Architecture`](../requirements/KFG-V1-REQUIREMENTS.md#26-service-architecture)
- [`§27 Service Ownership`](../requirements/KFG-V1-REQUIREMENTS.md#27-service-ownership)
- [`§37 Spring Boot Coding Standards`](../requirements/KFG-V1-REQUIREMENTS.md#37-spring-boot-coding-standards)

## Related ADRs

- [ADR-001: Use capability-oriented microservices for KFG V1](ADR-001-microservices-architecture.md)
- [ADR-003: Enforce database ownership per service](ADR-003-database-per-service-ownership.md)
