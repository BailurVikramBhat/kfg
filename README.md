# Kachiguda Financial Group

Kachiguda Financial Group (KFG) is a production-grade digital neo-banking
platform built as a learning and engineering project.

## Technology Baseline

- Java 25
- Spring Boot 4.1.x
- Maven
- PostgreSQL
- Apache Kafka
- Keycloak
- Redis
- Docker
- Kubernetes

## Architecture

KFG is implemented as independently deployable Spring Boot services with
strict service data ownership and event-driven integration where appropriate.

The financial core uses an immutable double-entry ledger.

## Repository Structure

- `services/` — KFG application services
- `simulators/` — external-system simulators
- `contracts/` — API and event contracts
- `platform/` — local and deployment infrastructure
- `testing/` — cross-service testing suites
- `docs/` — requirements, ADRs, architecture and runbooks
- `scripts/` — developer and operational scripts

## Requirements

The frozen KFG V1 requirements are available at:

`docs/requirements/KFG-V1-REQUIREMENTS.md`

## Build

### Windows

```powershell
.\mvnw.cmd verify
