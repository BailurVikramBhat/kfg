# ADR-008: Make KFG-to-KFG transfers atomic inside Ledger Service

## Status

Accepted

## Date

2026-07-25

## Context

An internal KFG transfer moves value between two accounts whose financial balances are both controlled by KFG.

A naïve microservice implementation could debit the source account, publish a message, and eventually credit the destination. That creates an avoidable period in which money has left one account without reaching the other and introduces compensation/reconciliation complexity for a transaction that KFG can perform within one authoritative ledger.

Because Ledger Service owns all KFG financial balances, both sides of an internal transfer can share one PostgreSQL transaction boundary.

## Decision

Every completed KFG-to-KFG transfer will be posted **atomically inside Ledger Service using one local PostgreSQL transaction**.

The posting flow is conceptually:

1. receive an authorised, idempotent posting instruction;
2. identify all affected ledger accounts;
3. lock affected balance rows in deterministic order;
4. validate account/currency/status and available funds;
5. create the journal;
6. create balanced debit and credit entries;
7. update affected financial balances;
8. create the outbox event;
9. mark/post the journal;
10. commit.

If any required step fails before commit, the complete transaction rolls back.

Ledger Service must prevent concurrent overspending and deadlocks through explicit locking and deterministic lock ordering.

The transfer is considered financially complete only after the ledger transaction commits.

## Rationale

KFG controls both accounts and therefore does not need eventual consistency between them.

A single local ACID transaction provides stronger and simpler correctness than an event-driven debit/credit saga for this use case.

Kafka remains useful after commit for notifications, audit ingestion, projections, and other side effects.

## Consequences

### Positive

- Source debit and destination credit cannot partially commit.
- Internal transfers do not require a compensation saga.
- Balance invariants are easier to reason about and test.
- Concurrent transfer correctness is centralised.
- Customer-facing completion status has a clear financial meaning.

### Negative / trade-offs

- Ledger Service is the critical posting bottleneck.
- Both affected KFG financial accounts must be represented inside the same Ledger Service/database ownership boundary.
- Correct row-locking strategy is mandatory.
- Very high scale may require careful ledger partitioning/sharding strategy in future versions.

### Operational implications

Internal-transfer tests must include heavy concurrency, duplicate idempotency requests, deadlock scenarios, balance reconciliation, and property-based accounting invariants.

## Alternatives considered

### Debit source, publish Kafka event, credit destination

Rejected because it intentionally introduces partial financial state and eventual consistency where KFG has the ability to commit atomically.

### Distributed transaction across Account Service and Ledger Service

Rejected because Account Service does not own financial balances and KFG avoids XA/two-phase commit.

### Payment Service writes ledger tables directly

Rejected because it violates Ledger Service ownership and lets orchestration bypass accounting invariants.

### Saga with compensating reversal

Sagas are useful for genuinely distributed external workflows, but unnecessary for an internal transfer entirely controlled by one ledger boundary.

## Guardrails

- Payment Service orchestrates but never writes ledger tables.
- Ledger Service must lock all affected financial balances before validating/posting.
- Lock order must be deterministic.
- A posted journal is immutable.
- Duplicate posting instructions must not create duplicate journals.
- A successful API result must correspond to a committed financial posting.

## Related requirements

- [`§11.5 Internal transfer`](../requirements/KFG-V1-REQUIREMENTS.md#115-internal-transfer)
- [`§12 Ledger and Accounting Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#12-ledger-and-accounting-requirements)
- [`§12.7 Financial posting transaction`](../requirements/KFG-V1-REQUIREMENTS.md#127-financial-posting-transaction)
- [`§12.8 Concurrency`](../requirements/KFG-V1-REQUIREMENTS.md#128-concurrency)
- [`§39.14 Distributed transaction policy`](../requirements/KFG-V1-REQUIREMENTS.md#3914-distributed-transaction-policy)

## Related ADRs

- [ADR-005: Ledger Service owns financial balances](ADR-005-ledger-owns-financial-balances.md)
- [ADR-007: Use the transactional outbox pattern](ADR-007-transactional-outbox.md)
