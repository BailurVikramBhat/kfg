# ADR-009: Preserve ambiguous IMPS outcomes as UNKNOWN and reconcile

## Status

Accepted

## Date

2026-07-25

## Context

External IMPS processing crosses a network and a system outside KFG's authoritative transaction boundary.

After KFG submits a transfer, an HTTP timeout or connection failure does not prove that the external network rejected the transaction. The remote system may have accepted and processed the request but failed to deliver the response.

Treating every timeout as failure could cause KFG to return funds and then later discover that the external transfer succeeded. Blindly retrying the money movement could cause duplicate transfer execution.

KFG therefore needs an explicit state representing genuine uncertainty.

## Decision

When an IMPS instruction has been submitted externally and KFG cannot determine the authoritative result, Payment Service will set the payment to **`UNKNOWN`** rather than mapping the condition to success or failure.

`UNKNOWN` means:

> KFG cannot currently prove whether the external payment succeeded or failed.

A reconciliation workflow owned by Payment Service will:

1. create/maintain a reconciliation case;
2. query the IMPS Network Simulator/provider using the existing external/payment reference;
3. record every attempt;
4. resolve to success when authoritative success is obtained;
5. resolve to failure and perform the required release/reversal when authoritative failure is obtained;
6. remain `UNKNOWN` and retry/escalate when the provider still cannot establish the result.

Operations users may requery or escalate an unresolved case but cannot arbitrarily mark the payment `COMPLETED`.

The original external financial instruction must not be automatically repeated merely because the synchronous call timed out.

## Rationale

This design preserves reality instead of converting a technical timeout into an invented financial fact.

It protects against duplicate transfers and prevents premature reversals. It also creates a clear operational model for stuck/ambiguous external payments.

## Consequences

### Positive

- Timeouts do not cause unsafe automatic retries.
- Duplicate external transfers are less likely.
- Financial uncertainty is visible and measurable.
- Operations receives a controlled workflow instead of manual database fixes.
- Recovery behaviour is deterministic and testable.

### Negative / trade-offs

- Customers may temporarily see a payment whose final outcome is unresolved.
- Funds may need to remain reserved/restricted until reconciliation resolves the outcome.
- Reconciliation workers, operational dashboards, alerts, and histories are required.
- Provider status APIs/references become important integration contracts.

### Operational implications

KFG must monitor:

- count and age of `UNKNOWN` payments;
- reconciliation attempt failures;
- stale `PROCESSING` payments;
- provider availability;
- unresolved cases beyond SLA.

## Alternatives considered

### Treat timeout as FAILED

Rejected because timeout is a transport observation, not authoritative proof of external failure.

### Automatically retry the original transfer request

Rejected because the first request may already have succeeded, causing duplicate money movement.

### Treat timeout as SUCCESS

Rejected because KFG has no evidence that the external network accepted the transfer.

### Manual operations resolution only

Rejected because ordinary ambiguity should be resolved automatically where the provider exposes status lookup. Manual operations remains an escalation path, not the primary correctness mechanism.

## Guardrails

- `UNKNOWN` is allowed only when an external outcome is genuinely ambiguous.
- A definite provider failure should resolve through the normal failed/reversal path.
- Reconciliation is idempotent.
- Requerying status must not submit a second financial instruction.
- Employees cannot directly invent an authoritative success state.
- Reversals are new ledger postings; original posted history is never rewritten.

## Related requirements

- [`§11.6 IMPS transfer`](../requirements/KFG-V1-REQUIREMENTS.md#116-imps-transfer)
- [`§11.7 Timeout semantics`](../requirements/KFG-V1-REQUIREMENTS.md#117-timeout-semantics)
- [`§11.8 Failure and reversal`](../requirements/KFG-V1-REQUIREMENTS.md#118-failure-and-reversal)
- [`§14 IMPS Reconciliation Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#14-imps-reconciliation-requirements)
- [`§39 Reliability and Resilience Requirements`](../requirements/KFG-V1-REQUIREMENTS.md#39-reliability-and-resilience-requirements)

## Related ADRs

- [ADR-005: Ledger Service owns financial balances](ADR-005-ledger-owns-financial-balances.md)
- [ADR-007: Use the transactional outbox pattern](ADR-007-transactional-outbox.md)
