# ADR-006: Use Kafka with Avro for asynchronous integration

## Status

Accepted

## Date

2026-07-25

## Context

KFG needs asynchronous communication for outcomes and side effects that do not belong in a synchronous critical transaction, including notifications, audit ingestion, projections, and operational event processing.

The system also needs explicit event contracts, schema evolution, retry/DLQ behaviour, correlation metadata, privacy controls, and duplicate-delivery handling.

Kafka must not become a substitute for transactional correctness in the financial core.

## Decision

KFG will use **Apache Kafka in KRaft mode** for asynchronous inter-service event delivery.

KFG event payloads will use **Avro schemas with Schema Registry** and a standard event envelope including at least:

- `eventId`
- `eventType`
- `eventVersion`
- `occurredAt`
- `producer`
- `correlationId`
- `causationId` where applicable
- event payload

Event delivery semantics are treated as **at least once**. Consumers that cause business effects must be idempotent.

Kafka is appropriate for:

- notifications;
- audit/security event ingestion;
- read projections;
- asynchronous downstream side effects;
- non-critical operational integration.

Kafka is **not** the mechanism used to debit one KFG account and later credit another KFG account.

Schema compatibility must be enforced so that producers cannot casually break existing consumers.

## Rationale

Kafka provides durable asynchronous delivery and decouples critical domain commits from slower or independently failing consumers.

Avro and Schema Registry force event contracts to be explicit and versioned, which is important in a multi-service banking platform.

At-least-once semantics are deliberately acknowledged rather than relying on a misleading assumption of end-to-end exactly-once business processing.

## Consequences

### Positive

- Services can publish outcomes without waiting for every side effect.
- Notification/audit failures do not roll back completed financial operations.
- Consumers can scale independently.
- Event contracts are versioned and reviewable.
- Replay/reprocessing patterns become possible where designed safely.

### Negative / trade-offs

- Kafka adds infrastructure and operational complexity.
- Duplicate delivery must be handled.
- Schema governance is required.
- Consumer lag and DLQs need monitoring.
- Eventual consistency is visible in asynchronous projections.

### Operational implications

KFG needs topic bootstrap, compatibility rules, consumer-lag metrics, retry/DLQ strategies, event privacy review, and contract/integration tests using real Kafka.

## Alternatives considered

### Synchronous REST for all integrations

Rejected because notifications, audit ingestion, and projections should not lengthen critical request paths or make unrelated availability a prerequisite for completed banking operations.

### JSON events without schema governance

Rejected because loose JSON contracts make breaking changes easy and shift failures to runtime.

### Kafka for internal money movement

Rejected because KFG-to-KFG money movement can be completed more safely inside one ACID Ledger Service transaction.

### RabbitMQ

A valid messaging platform, but Kafka better matches KFG's durable event-streaming, replay, schema-governance, and learning goals.

## Guardrails

- Events must not contain unnecessary sensitive PII.
- Event contracts are independent of JPA entities.
- Important consumers use inbox/idempotency handling.
- Poison messages must not retry forever.
- Business services must never assume a Kafka event is delivered exactly once.
- Financial posting is not delegated to an eventually consistent debit/credit event chain for internal transfers.

## Related requirements

- [`§31 Kafka and Event Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#31-kafka-and-event-requirements)
- [`§33 Consumer Inbox / Idempotency Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#33-consumer-inbox--idempotency-requirements)
- [`§39.8 Kafka outage`](../requirements/KFG-V1-REQUIREMENTS.md#398-kafka-outage)
- [`§41.9 Kafka integration tests`](../requirements/KFG-V1-REQUIREMENTS.md#419-kafka-integration-tests)

## Related ADRs

- [ADR-007: Use the transactional outbox pattern](ADR-007-transactional-outbox.md)
- [ADR-008: Make KFG-to-KFG transfers atomic inside Ledger Service](ADR-008-internal-transfer-atomicity.md)
