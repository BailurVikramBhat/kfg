# KFG Architecture Decision Records

Architecture Decision Records (ADRs) capture significant technical decisions for **Kachiguda Financial Group Inc. (KFG)**.

The goal is not to document every implementation detail. An ADR exists when a decision has meaningful architectural consequences, affects multiple parts of the system, establishes a long-lived engineering constraint, or would be expensive to reverse without understanding the original rationale.

## Status values

- **Proposed** — under active discussion; not yet binding.
- **Accepted** — current KFG architecture.
- **Superseded** — replaced by a newer ADR. The newer ADR must be linked.
- **Deprecated** — retained for history but no longer recommended.

Accepted ADRs are not silently rewritten when the architecture changes. A material reversal or replacement should be recorded in a new ADR, and the old ADR should be marked as superseded.

## Decision index

| ADR | Decision | Status |
|---|---|---|
| [ADR-001](ADR-001-microservices-architecture.md) | Use capability-oriented microservices for KFG V1 | Accepted |
| [ADR-002](ADR-002-monorepo.md) | Use a monorepo for KFG V1 | Accepted |
| [ADR-003](ADR-003-database-per-service-ownership.md) | Enforce database ownership per service | Accepted |
| [ADR-004](ADR-004-keycloak-for-identity.md) | Use Keycloak as the identity provider | Accepted |
| [ADR-005](ADR-005-ledger-owns-financial-balances.md) | Ledger Service owns financial balances | Accepted |
| [ADR-006](ADR-006-kafka-avro-eventing.md) | Use Kafka with Avro for asynchronous integration | Accepted |
| [ADR-007](ADR-007-transactional-outbox.md) | Use the transactional outbox pattern | Accepted |
| [ADR-008](ADR-008-internal-transfer-atomicity.md) | Make KFG-to-KFG transfers atomic inside Ledger Service | Accepted |
| [ADR-009](ADR-009-imps-unknown-and-reconciliation.md) | Preserve ambiguous IMPS outcomes as UNKNOWN and reconcile | Accepted |
| [ADR-010](ADR-010-testcontainers-integration-testing.md) | Use Testcontainers with production-like dependencies | Accepted |

## ADR rules

1. Keep each ADR focused on one architectural decision.
2. Record the problem and constraints before the decision.
3. Include meaningful consequences, including drawbacks.
4. Record realistic alternatives that were considered.
5. Link related requirements and ADRs.
6. Do not use ADRs as implementation tickets or API documentation.
7. Do not edit an accepted ADR to hide a later architectural change.

## Creating a new ADR

Copy [`TEMPLATE.md`](TEMPLATE.md), allocate the next sequential number, and use a concise kebab-case filename:

```text
ADR-011-example-decision.md
```

Where possible, the title should describe the decision rather than the topic.

## Source of truth

The frozen product and engineering requirements are maintained in:

[`KFG-V1-REQUIREMENTS.md`](../requirements/KFG-V1-REQUIREMENTS.md)
