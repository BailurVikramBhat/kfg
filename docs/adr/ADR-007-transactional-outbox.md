# ADR-007: Use the transactional outbox pattern

## Status

Accepted

## Date

2026-07-25

## Context

A KFG business operation may need to update its PostgreSQL state and publish a Kafka event.

Publishing directly to Kafka after committing the database creates a crash window:

1. database commit succeeds;
2. process crashes before publishing;
3. business state exists but the event is permanently lost.

Publishing before committing creates the opposite problem: consumers may observe an event for a database transaction that later rolls back.

KFG does not use distributed XA transactions across PostgreSQL and Kafka.

## Decision

Services that must reliably publish an event after a local business transaction will use the **transactional outbox pattern**.

Within the same local PostgreSQL transaction, a service will:

1. perform the business mutation;
2. insert an explicit integration/domain event into its `outbox_event` table;
3. commit both together.

A separate outbox publisher will claim pending rows and publish them to Kafka.

The initial KFG implementation will use a **polling outbox publisher**. Multiple publisher instances must use a safe claiming mechanism such as `FOR UPDATE SKIP LOCKED` or an equivalent proven approach.

Publication failures update retry metadata without rolling back the already-completed business transaction.

Consumers still treat Kafka delivery as at least once and remain idempotent because a publish acknowledgement/crash window can produce duplicate deliveries.

Debezium/CDC may be adopted later through a new ADR if operational needs justify it.

## Rationale

The outbox pattern gives KFG atomicity between local state and the intent to publish without introducing two-phase commit.

It also behaves safely during Kafka outages: financial/database operations that are allowed to complete can commit while their outbox events remain pending until Kafka recovers.

## Consequences

### Positive

- No lost event caused by the database-commit/Kafka-publish gap.
- Kafka outages do not necessarily corrupt committed business state.
- Publication can retry independently.
- Outbox backlog is inspectable and measurable.
- Pattern works with ordinary PostgreSQL transactions.

### Negative / trade-offs

- Events are not published instantaneously; polling introduces small latency.
- Outbox tables require cleanup/retention.
- Duplicate publication remains possible and must be tolerated.
- Publisher concurrency and retry logic require careful design.
- Every producing service gains additional persistence and operational metrics.

### Operational implications

Monitor:

- pending outbox count;
- oldest pending event age;
- publish failure count;
- retry attempts;
- DLQ/escalation where appropriate.

## Alternatives considered

### Database commit followed by direct Kafka publish

Rejected because a crash can permanently lose an event.

### Kafka publish followed by database commit

Rejected because consumers can observe events for rolled-back state.

### XA / two-phase commit

Rejected because KFG's distributed-transaction policy favours local ACID transactions plus explicit reliable messaging, compensation, idempotency, and reconciliation.

### Debezium from day one

A strong option for larger deployments, but rejected initially because polling is easier to understand, test, debug, and operate while KFG is being built.

## Guardrails

- The outbox row must be created in the same local transaction as the business change.
- Outbox payloads are explicit event contracts, never serialised JPA entities.
- Publisher retry does not repeat the original business operation.
- Consumers remain idempotent.
- Outbox backlog must be observable.
- Published rows must not be full-scanned indefinitely; appropriate indexes/retention are required.

## Related requirements

- [`§32 Transactional Outbox Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#32-transactional-outbox-requirements)
- [`§39.8 Kafka outage`](../requirements/KFG-V1-REQUIREMENTS.md#398-kafka-outage)
- [`§41.10 Outbox tests`](../requirements/KFG-V1-REQUIREMENTS.md#4110-outbox-tests)

## Related ADRs

- [ADR-006: Use Kafka with Avro for asynchronous integration](ADR-006-kafka-avro-eventing.md)
- [ADR-008: Make KFG-to-KFG transfers atomic inside Ledger Service](ADR-008-internal-transfer-atomicity.md)
