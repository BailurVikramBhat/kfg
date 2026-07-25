# ADR-005: Ledger Service owns financial balances

## Status

Accepted

## Date

2026-07-25

## Context

KFG separates account metadata from financial accounting. Account Service needs to own account identity, customer ownership, account state, and non-financial metadata, while Ledger Service must enforce accounting invariants, immutable journals, available funds, holds, reversals, and concurrent posting.

Allowing Account Service and Ledger Service to maintain independently writable balance values would create two sources of financial truth and make inconsistencies inevitable.

## Decision

**Ledger Service is the sole owner of financial balances and financial postings.**

Ledger Service owns:

- ledger accounts;
- journals;
- journal entries;
- posted financial transactions;
- ledger balances;
- available balances;
- holds;
- reversal journals;
- adjustment postings;
- accounting system accounts;
- financial posting idempotency.

Account Service owns:

- account identifier and customer-facing account number;
- customer/account relationship;
- account type and currency metadata;
- operational account status;
- opening/closure metadata.

The financial balance is an optimised materialisation maintained by Ledger Service from posted ledger activity. It is not an independently editable business value.

No KFG service or employee workflow may directly mutate a customer's balance. Manual corrections must create controlled ledger adjustment or reversal journals.

## Rationale

Financial correctness requires one authoritative posting boundary.

Co-locating journals, affected balance rows, locks, holds, and the outbox within Ledger Service allows a posting to be committed atomically in one PostgreSQL transaction.

This also makes balance reconciliation possible: persisted balances can be checked against ledger history instead of becoming an opaque source of truth.

## Consequences

### Positive

- One authoritative financial model.
- No balance drift caused by multiple writers.
- Internal transfers can be atomic.
- Reversals preserve immutable history.
- Financial concurrency can be handled centrally.
- Ledger invariants can be tested and monitored independently.

### Negative / trade-offs

- Account Service cannot independently answer authoritative balance writes.
- Balance reads may require Ledger Service or a controlled projection.
- Ledger Service becomes a high-criticality service requiring strict availability, testing, and security.
- Posting throughput is constrained by correctness-oriented locking and transaction semantics.

### Operational implications

Ledger Service requires strong observability, invariant monitoring, backup/restore testing, concurrency tests, property-based tests, and restricted database access.

## Alternatives considered

### Store balance in Account Service

Rejected because account metadata and accounting would then be split across services while Account Service remained capable of creating financial state independently.

### Store balance in both Account and Ledger

Rejected because dual writable representations inevitably diverge and create reconciliation ambiguity.

### Compute balance from all journal entries on every read

Rejected for ordinary account reads because financial history may become very large. Ledger Service instead maintains balances transactionally and periodically reconciles them against ledger history.

### Redis as a balance source

Rejected. Redis may cache non-authoritative data but must never become the source of truth for money.

## Guardrails

- Only Ledger Service may write ledger and balance tables.
- Posted journal entries cannot be updated or deleted.
- Corrections are represented by new reversal/adjustment journals.
- Every posted journal must balance.
- Balance changes and journal postings occur in the same local transaction.
- Employees have no direct balance-edit capability.

## Related requirements

- [`§9.6 Balances`](../requirements/KFG-V1-REQUIREMENTS.md#96-balances)
- [`§12 Ledger and Accounting Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#12-ledger-and-accounting-requirements)
- [`§19 Manual Financial Adjustment Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#19-manual-financial-adjustment-requirements)
- [`§27.5 Account Service`](../requirements/KFG-V1-REQUIREMENTS.md#275-account-service)
- [`§27.6 Ledger Service`](../requirements/KFG-V1-REQUIREMENTS.md#276-ledger-service)

## Related ADRs

- [ADR-003: Enforce database ownership per service](ADR-003-database-per-service-ownership.md)
- [ADR-008: Make KFG-to-KFG transfers atomic inside Ledger Service](ADR-008-internal-transfer-atomicity.md)
