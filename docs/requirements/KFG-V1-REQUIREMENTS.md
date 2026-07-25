# Kachiguda Financial Group Inc. (KFG)
## V1 Product, Functional, Architecture, Security, Reliability and Quality Requirements

**Document status:** V1 scope frozen  
**Project:** Kachiguda Financial Group Inc. (KFG)  
**System type:** Web-first digital neo-bank simulation  
**Primary backend stack:** Java 25, Spring Boot (latest stable release compatible with Java 25 at project bootstrap, then pinned), Maven  
**Primary currency:** INR  
**Architecture target:** Production-grade microservices simulation, Level 2.5  
**Audience:** Product owner, backend developer, frontend developer/AI tooling, QA, operations, security and future contributors

---

# 1. Purpose

This document is the frozen V1 requirements baseline for Kachiguda Financial Group Inc. (KFG), a production-grade digital banking simulation intended to demonstrate serious backend engineering practices rather than a CRUD-only portfolio application.

KFG V1 will provide secure individual retail banking with simulated KYC, savings accounts, beneficiaries, internal KFG transfers, simulated IMPS transfers, immutable double-entry accounting, statements, notifications and a complete employee back-office experience.

The project deliberately includes production-grade concerns such as security, auditability, idempotency, concurrency control, resilient distributed workflows, event-driven integration, observability, automated testing, reproducible infrastructure and controlled deployment.

KFG V1 will **not** connect to real financial networks or process real money.

---

# 2. Product Vision

KFG is a fully digital, web-first neo-bank for:

1. **Individual retail customers**
2. **Internal KFG employees**

There are no physical branches in V1.

The product must allow a customer to securely onboard, complete simulated KYC, receive a savings account, add beneficiaries, move money internally or through a simulated IMPS network, see transaction history, generate statements and receive mandatory transactional/security notifications.

Internal employees must be able to operate the bank through controlled KYC, support, account restriction, reconciliation, approval and adjustment workflows without bypassing financial invariants.

---

# 3. V1 Scope Summary

## 3.1 In scope

- Individual retail customers
- Internal KFG employees
- Web-first banking
- Keycloak-based identity and authentication
- Mandatory MFA/TOTP
- Digital onboarding
- Simulated KYC verification and manual review
- Savings accounts only
- INR only
- KFG-to-KFG internal transfers
- Simulated external IMPS transfers
- Beneficiary management with cooling period
- Double-entry immutable ledger
- Ledger and available balances
- Holds, reversals and adjustments
- Transaction idempotency
- Reconciliation for ambiguous IMPS outcomes
- Back-office operations
- Maker-checker approvals
- Customer support cases and SLAs
- Audit trail and privileged-access auditing
- Email, mock SMS and in-app notifications
- PDF and CSV statements
- Savings interest posting as a later V1 milestone
- Kafka event-driven integration
- Transactional outbox and consumer inbox/idempotency
- PostgreSQL database ownership per service
- Redis for non-authoritative distributed state
- MinIO/S3-compatible object storage for KYC documents
- Spring Cloud Gateway
- OpenTelemetry, Micrometer, Prometheus and Grafana
- Docker Compose local environment
- Kubernetes deployment later in V1
- CI/CD through GitHub Actions
- Extensive automated testing

## 3.2 Explicitly out of scope for V1

- Physical branches
- Business customers
- Current accounts
- Joint accounts
- Minor accounts
- Fixed deposits
- Recurring deposits
- Overdrafts
- Debit/credit cards
- Loans
- NEFT
- RTGS
- UPI
- International transfers
- Scheduled transfers
- Recurring transfers
- Customer-initiated transfer reversal
- Real NPCI/RBI integrations
- Real Aadhaar/PAN verification
- Video KYC
- Face biometrics
- Real fraud scoring engine
- Nominee settlement
- Estate/inheritance processing
- Multi-currency banking
- Production regulatory certification
- Multi-region active-active deployment
- Full event sourcing
- Enterprise BPM/workflow engine in V1

---

# 4. Personas

## 4.1 Customer

An adult individual retail customer using KFG through the customer web application.

## 4.2 Customer Support

Handles general customer queries, support cases and limited emergency account-protection actions.

## 4.3 KYC Officer

Reviews KYC cases and documents and may approve, reject or request more information within assigned authority.

## 4.4 KYC Manager

Handles KYC escalations and controlled overrides.

## 4.5 Operations Officer

Handles operational cases, reconciliation and controlled financial requests.

## 4.6 Operations Manager

Approves sensitive operational requests and higher-value adjustments.

## 4.7 Compliance Officer

Handles sensitive account restrictions, escalations and high-risk approvals.

## 4.8 Auditor

Read-only access to authorised audit, operational and financial evidence. The auditor must not receive mutation permissions.

## 4.9 System Administrator

Manages employee access, role assignment and system administration within audited and controlled boundaries.

---

# 5. Customer Lifecycle

Supported customer states:

- `ONBOARDING`
- `KYC_PENDING`
- `KYC_REVIEW`
- `KYC_REJECTED`
- `ACTIVE`
- `SUSPENDED`
- `BLOCKED`
- `CLOSED`
- `DECEASED`

Rules:

- Customer records are never physically deleted as a normal closure operation.
- Historical financial and audit records must remain available according to retention policies.
- `SUSPENDED` represents temporary operational restriction.
- `BLOCKED` represents stronger security/compliance restriction requiring employee intervention.
- `DECEASED` blocks outgoing financial activity and requires back-office handling.
- Nominee/estate settlement is outside V1.

---

# 6. Identity and Authentication Requirements

## 6.1 Identity provider

Keycloak owns:

- Username/email authentication
- Password storage and hashing
- MFA/TOTP credentials
- Session lifecycle
- Token issuance
- Refresh tokens
- Password reset
- Email verification
- Brute-force protection
- Account lockout capabilities

KFG application databases must never store customer passwords or TOTP secrets.

## 6.2 Realms

Use separate Keycloak realms for:

- KFG customers
- KFG employees

This separation allows distinct session, MFA, password and administrative policies.

## 6.3 Frontend authentication

Use OAuth 2.0 / OpenID Connect Authorization Code Flow with PKCE.

The frontend must not collect credentials for forwarding to a custom KFG login endpoint.

## 6.4 MFA

- TOTP enrollment is mandatory for customers.
- TOTP is mandatory for employees.
- Recovery codes must be single-use, stored hashed and invalidated when regenerated.
- Lost-MFA recovery must require controlled identity verification.
- Employee MFA reset must be privileged and audited.

## 6.5 Session security

- Access tokens must be short-lived and configurable.
- Refresh-token rotation must be enabled where supported/configured.
- Suspicious refresh-token reuse must generate a security event.
- Customer active-session count must be configurable.
- Employee sessions must use stricter limits and shorter idle timeouts.
- Customers must be able to view/revoke active sessions where supported by the identity design.

## 6.6 Service-to-service authentication

Internal synchronous calls must use explicit service identity, initially OAuth2 Client Credentials through Keycloak.

An internal service must not be trusted solely because it is on the private network.

User bearer tokens must not be blindly forwarded to every downstream service.

## 6.7 Actor context

Trusted internal calls must propagate relevant accountability context such as:

- actor type
- actor ID
- correlation ID
- request ID

Browser-supplied headers must not be blindly treated as trusted actor context.

---

# 7. Customer Registration and Profile Requirements

## 7.1 Required customer information

KFG V1 captures:

- First name
- Middle name, optional
- Last name
- Date of birth
- Gender
- Email
- Mobile number
- Residential address
- PAN-like identifier
- Aadhaar-like identifier
- Occupation
- Annual income range
- Nationality

Only synthetic identifiers may be used in development/test environments.

## 7.2 Age

Minimum onboarding age: 18.

Minor accounts are out of scope.

## 7.3 Identifiers

Every customer has:

- immutable internal UUID `customerId`
- immutable customer-facing `customerNumber`

Email, mobile and PAN lookup identity are globally unique in V1.

## 7.4 Onboarding identity sequence

Preferred flow:

1. Start registration
2. Verify email/mobile as required
3. Create Keycloak identity early
4. Keep customer in onboarding lifecycle
5. Complete profile
6. Submit KYC
7. Activate customer after KYC approval

## 7.5 Profile updates

Customers may directly change approved low-risk fields such as:

- address
- occupation
- income range
- communication preferences

Sensitive fields such as legal name, email, mobile and PAN-like identity require stronger verification and/or employee review.

---

# 8. KYC Requirements

## 8.1 Supported KYC artifacts

V1 simulates:

- PAN
- Aadhaar
- Address proof
- Profile photo

Optional future liveness/video KYC is excluded.

## 8.2 Storage

- Binary documents are stored in private MinIO/S3-compatible object storage.
- PostgreSQL stores document metadata only.
- Objects must not be publicly accessible.
- Access uses authorised backend flows or short-lived signed URLs.
- Document viewing by employees is audited.

## 8.3 Verification outcome

Automated provider simulation returns:

- `PASS`
- `REVIEW`
- `FAIL`

Outcomes map into KFG-controlled KYC workflow states.

## 8.4 Manual review

KYC officers must be able to:

- claim a case
- review documents
- approve
- reject
- request more information

An override of an automated failure requires maker-checker approval.

## 8.5 Work queue states

Typical queue states:

- `UNASSIGNED`
- `ASSIGNED`
- `IN_REVIEW`
- `MORE_INFORMATION_REQUIRED`
- `APPROVED`
- `REJECTED`

## 8.6 KYC outage behaviour

Failure of the mock external verifier must not automatically reject the customer. The case remains pending/retriable according to workflow rules.

---

# 9. Account Requirements

## 9.1 Account types

V1 supports only:

- Savings account
- Currency: INR

Current accounts are deferred until business banking exists.

## 9.2 Automatic account creation

A successfully approved customer automatically receives one savings account.

Maximum V1 account count per customer:

- one savings account

## 9.3 Identifiers

Every account has:

- internal UUID `accountId`
- immutable unique customer-facing 12-digit account number

## 9.4 Account states

- `PENDING_ACTIVATION`
- `ACTIVE`
- `DEBIT_BLOCKED`
- `CREDIT_BLOCKED`
- `FROZEN`
- `DORMANT`
- `CLOSURE_PENDING`
- `CLOSED`

## 9.5 Minimum balance and overdraft

- Minimum balance: ₹0
- No overdraft in V1
- A transfer that would exceed available balance must be rejected

## 9.6 Balances

KFG distinguishes:

- ledger balance
- available balance
- held amount

The authoritative balance state belongs to the Ledger Service, not Account Service.

## 9.7 Dormancy

Dormant-account state is modelled in V1. Automatic dormancy processing may be introduced later in V1 with a configurable inactivity threshold.

---

# 10. Beneficiary Requirements

## 10.1 Types

- `KFG_INTERNAL`
- `IMPS_EXTERNAL`

## 10.2 Internal beneficiary data

- destination KFG account number
- resolved account-holder name

The customer must not be able to supply a fake registered KFG account-holder name.

## 10.3 External beneficiary data

- account number
- IFSC
- beneficiary name
- bank name

External validation is simulated.

## 10.4 Lifecycle

- `PENDING_VERIFICATION`
- `COOLING_PERIOD`
- `ACTIVE`
- `BLOCKED`
- `DELETED`

## 10.5 Security

Beneficiary creation requires step-up authentication/transaction confirmation.

## 10.6 Cooling period

Cooling period is configurable.

Suggested defaults:

- local/test: 30 minutes or shorter test-controlled time
- production-like: 24 hours

Tests must use injected clocks rather than sleeping.

## 10.7 Deletion

Beneficiary deletion is logical, not physical. Historical transactions must remain resolvable.

---

# 11. Payment Requirements

## 11.1 Supported payment types

V1 supports:

1. KFG-to-KFG internal transfer
2. Simulated external IMPS transfer

## 11.2 Payment model

Payment workflow state and ledger accounting are separate concepts.

A payment record includes at minimum:

- payment ID
- customer-facing payment reference
- customer ID
- source account ID
- beneficiary ID
- payment type
- amount
- currency
- status
- idempotency key/reference
- external reference where applicable
- failure code where applicable
- created/completed timestamps

## 11.3 Statuses

- `INITIATED`
- `PENDING`
- `PROCESSING`
- `COMPLETED`
- `FAILED`
- `UNKNOWN`
- `REVERSED`
- `CANCELLED` where applicable before irrevocable processing

`UNKNOWN` is mandatory for ambiguous external-payment outcomes.

## 11.4 Limits

Configurable policies include:

- per-transaction limit
- daily aggregate limit
- new-beneficiary limit

Illustrative KFG defaults may use values such as ₹2,00,000 per transaction, ₹5,00,000 daily and ₹50,000 for a newly activated beneficiary, but exact values must be configuration rather than hard-coded domain constants.

## 11.5 Internal transfer

KFG-to-KFG transfer must complete through one authoritative ledger transaction boundary.

High-level flow:

1. Authenticate/authorise customer
2. Validate account ownership/status
3. Validate beneficiary
4. Validate limits
5. Enforce idempotency
6. Post atomic balanced journal in Ledger Service
7. Persist payment completion
8. Persist outbox event
9. Return success

Kafka must not sit in the critical financial transaction path for internal money movement.

## 11.6 IMPS transfer

IMPS processing uses an external network simulator.

Possible outcomes:

- success
- business rejection
- timeout
- delayed response
- HTTP/server failure
- network unavailable
- duplicate response
- ambiguous/unknown
- late success
- late failure

The customer request may return `202 Accepted` with `PROCESSING` where appropriate.

## 11.7 Timeout semantics

A timeout does not imply failure.

If the external outcome is uncertain:

- mark the payment `UNKNOWN`
- do not blindly retry the money movement
- reconcile against the external network

## 11.8 Failure and reversal

A definite external failure after a reserved/debited state must cause a ledger-based reversal/release.

Original posted accounting history must remain immutable.

## 11.9 Customer reversal

Customers cannot simply undo a completed transfer in V1.

Controlled employee reversal/adjustment mechanisms are separate privileged workflows.

---

# 12. Ledger and Accounting Requirements

## 12.1 Core rule

KFG uses an immutable double-entry ledger.

For every posted journal:

`sum(debits) = sum(credits)`

No exception.

## 12.2 Source of truth

The ledger is the source of financial truth.

A balance is an optimised/materialised representation of ledger state, not an independently editable truth.

## 12.3 Ledger ownership

Only Ledger Service may write:

- ledger accounts
- journals
- journal entries
- financial balances
- holds
- reversals

No other service may write ledger tables directly.

## 12.4 Journal entries

Each entry has:

- entry direction: `DEBIT` or `CREDIT`
- positive monetary amount

Negative entry amounts are prohibited.

## 12.5 Immutability

Once a journal is posted:

- journal facts are immutable
- entries are immutable
- correction is represented by a new journal/reversal

No ordinary update/delete API exists for posted financial records.

## 12.6 System ledger accounts

V1 may include accounts such as:

- customer deposit liability
- IMPS clearing
- interest expense
- adjustment clearing
- controlled test funding

These allow accounting events to remain balanced rather than creating/destroying value via direct balance mutation.

## 12.7 Financial posting transaction

A posting transaction should conceptually:

1. begin local PostgreSQL transaction
2. lock affected balance rows in deterministic order
3. validate available funds
4. create journal
5. create entries
6. update authoritative balance projections
7. mark journal posted
8. create outbox event
9. commit

Any failure rolls back the full local financial transaction.

## 12.8 Concurrency

Concurrent transfers must never overdraw a savings account.

Example invariant:

- balance ₹10,000
- two concurrent ₹8,000 transfers
- exactly one may succeed

Financial posting will use explicit database concurrency control, initially favouring PostgreSQL row locks such as `SELECT ... FOR UPDATE`, with deterministic lock ordering to reduce deadlocks.

## 12.9 Isolation

Default PostgreSQL isolation may remain `READ COMMITTED` when paired with correct explicit locking and constraints.

Global `SERIALIZABLE` isolation is not required without evidence.

## 12.10 Financial precision

Java: `BigDecimal`  
Database: `NUMERIC/DECIMAL`, target `NUMERIC(19,4)` for ledger storage  
Customer-facing INR transfer validation: maximum two fractional digits

Never use `float` or `double` for money.

## 12.11 Holds

Ledger supports explicit holds with states such as:

- `ACTIVE`
- `CAPTURED`
- `RELEASED`
- `EXPIRED`

## 12.12 Reconciliation checks

KFG must support consistency verification for:

- balanced journals
- balance projections vs ledger-derived balances
- duplicate posting detection
- journal-entry integrity
- payment-ledger references

Any ledger imbalance is a critical incident.

---

# 13. Idempotency Requirements

Money-moving APIs require an `Idempotency-Key`.

Idempotency records bind at least:

- actor/customer
- endpoint/operation
- idempotency key
- request hash

Rules:

- same key + same request => return the prior logical result
- same key + different request => conflict
- concurrent submissions with the same key must result in one financial instruction
- Redis may accelerate lookup, but persistent storage is authoritative
- idempotency retention is configurable; suggested API retention is 24 hours or longer as required

---

# 14. IMPS Reconciliation Requirements

Reconciliation is initially implemented within the Payment Service/domain rather than a separate microservice.

It handles:

- stale `PROCESSING` payments
- `UNKNOWN` payments
- internal/external status mismatch
- missing/late acknowledgements
- duplicate callbacks
- late success/failure

Employees may:

- view unresolved cases
- requery the simulated network
- escalate
- view reconciliation history

Employees may not manually invent a successful financial outcome without authoritative evidence.

Reconciliation actions must be idempotent.

---

# 15. Savings Interest Requirements

Savings interest is included as a later V1 milestone after payment/ledger stability.

Requirements:

- configurable annual rate
- daily calculation
- monthly posting
- idempotent batch execution
- ledger-based posting
- system interest-expense account
- customer interest-credit entry

Complex slabs, promotions and tax deduction are outside initial V1.

---

# 16. Back-Office Requirements

## 16.1 Employee lifecycle

Supported states:

- `INVITED`
- `ACTIVE`
- `SUSPENDED`
- `LOCKED`
- `NOTICE_PERIOD`
- `TERMINATED`

A terminated employee immediately loses access while historical records retain their employee ID.

## 16.2 Roles

- `CUSTOMER_SUPPORT`
- `KYC_OFFICER`
- `KYC_MANAGER`
- `OPERATIONS_OFFICER`
- `OPERATIONS_MANAGER`
- `COMPLIANCE_OFFICER`
- `AUDITOR`
- `SYSTEM_ADMIN`

KFG favours fine-grained permissions underneath roles.

## 16.3 Permission model

Authorisation must use fine-grained authorities such as:

- `CUSTOMER_READ`
- `KYC_CASE_REVIEW`
- `KYC_CASE_APPROVE`
- `ACCOUNT_FREEZE`
- `ADJUSTMENT_REQUEST`
- `ADJUSTMENT_APPROVE`

Code should prefer checking business permissions over scattering hard-coded role names.

## 16.4 Least privilege

Customer-support users do not automatically see KYC documents or highly sensitive data.

Employees receive only the minimum permissions required for their role.

## 16.5 Search

Authorised employee search supports:

- customer number
- account number
- email
- mobile
- payment reference

Sensitive data is masked by default.

## 16.6 No impersonation

Employees must not log in as a customer or initiate an ordinary customer transfer.

They use dedicated back-office representations and privileged, audited operational workflows.

---

# 17. Maker-Checker / Approval Requirements

KFG V1 includes a generic approval domain.

Used for:

- manual financial adjustment
- account unfreeze
- KYC override
- transfer-limit modification
- selected privileged operations

Core rules:

- maker must not equal checker
- checker cannot alter the request payload
- exact request snapshot must be preserved
- requests may expire
- maker may cancel while still pending when permitted
- approved requests execute at most once
- rejection prevents execution
- permission checks apply to checker
- all actions are audited
- architecture supports multiple approval levels where needed

---

# 18. Account Restriction and Emergency Action Requirements

## 18.1 Emergency debit block

Customer Support may apply an immediate temporary debit block for reported compromise.

Required metadata:

- employee ID
- reason code
- timestamp
- expiry/escalation

## 18.2 Freeze

Permanent/strong freezes follow controlled operations/compliance workflows.

## 18.3 Unfreeze

Account unfreeze requires maker-checker approval.

## 18.4 Blocked customer read access

A blocked customer may retain read-only access to permitted information such as balances and statements while money movement and sensitive changes are prevented.

---

# 19. Manual Financial Adjustment Requirements

Employees must never directly edit a balance.

Adjustment workflow:

1. maker creates adjustment request
2. request specifies account, debit/credit, amount, reason code and justification
3. authorised checker approves
4. controlled financial instruction reaches Ledger Service
5. Ledger Service posts an `ADJUSTMENT` journal
6. audit event generated
7. customer notified where required

Role/value thresholds are configurable.

Architecture supports higher approval levels for high-value operations.

---

# 20. Support Case Management Requirements

Supported case categories may include:

- `FAILED_PAYMENT`
- `UNKNOWN_PAYMENT`
- `ACCOUNT_ACCESS`
- `KYC`
- `PROFILE_CHANGE`
- `ACCOUNT_FREEZE`
- `GENERAL`

Case states:

- `OPEN`
- `IN_PROGRESS`
- `WAITING_CUSTOMER`
- `RESOLVED`
- `CLOSED`

Cases include:

- case number
- priority
- assigned team
- assigned employee
- related customer
- optional related account/payment/beneficiary
- SLA due timestamp
- breach timestamp
- append-style internal notes

Employees may claim unassigned work. Managers may reassign.

---

# 21. Audit Requirements

## 21.1 Audit vs logs

These are separate concerns:

- application logs: diagnostics/operations
- audit records: who did what to what and when
- security events: authentication/authorisation/security activity

## 21.2 Audit events

Sensitive/privileged actions include at least:

- customer status changes
- employee customer searches where required
- KYC document access
- sensitive PII reveal
- KYC approvals/overrides
- account blocks/freezes/unfreezes
- approvals and rejections
- financial adjustments
- employee role/permission changes
- MFA-reset operations
- export requests

## 21.3 Audit immutability

Audit is append-only through normal application interfaces.

No update/delete API exists for audit records.

Later hardening may include hash chaining or WORM-style storage.

## 21.4 Metadata

Where appropriate capture:

- actor
- action
- target resource
- timestamp
- result
- reason
- correlation ID
- trace ID
- request ID
- source service
- IP
- user agent
- safe before/after metadata

Never store passwords, tokens, OTPs or highly sensitive raw values in audit metadata.

---

# 22. Notification Requirements

V1 channels:

- email
- mock SMS
- in-app notification

Mandatory notifications include events such as:

- KYC status change
- account activation
- login/new-device security alert
- password changed
- MFA changed/reset
- email/mobile change
- beneficiary created/activated
- payment completed
- payment failed
- payment reversed
- account frozen
- customer status changed

Mandatory transactional/security notifications cannot be disabled by customer marketing preferences.

Notification failure must never roll back an already successful financial transaction.

Notification processing is Kafka-driven and idempotent.

---

# 23. Statement and Transaction History Requirements

## 23.1 Transaction history

Customer view includes:

- date/time
- transaction reference
- transaction type
- debit/credit direction
- amount
- status
- description
- counterparty snapshot
- running/closing balance where applicable

Supported filters:

- date range
- type
- status
- debit/credit
- amount range
- reference search

Pagination is mandatory.

## 23.2 Statements

V1 supports:

- monthly statement
- custom date-range statement

Formats:

- PDF
- CSV

Statements include:

- opening balance
- transaction lines
- debit/credit values
- running balance
- closing balance
- customer/account details as appropriate

Large statement generation may be asynchronous.

Statement data is a read model/projection and must remain reconcilable against authoritative ledger data.

---

# 24. Customer Closure Requirements

Customer relationship closure requires at minimum:

- zero eligible balance
- no active hold
- no unresolved payment
- no pending financial adjustment
- no other blocking condition

Closure changes account/customer lifecycle state; it does not delete financial history.

---

# 25. Deceased Customer Requirements

Authorised employees may mark a customer `DECEASED`.

Effects:

- outgoing transactions blocked
- accounts appropriately restricted/frozen
- full audit trail retained

Nominee/estate settlement is deferred.

---

# 26. Service Architecture

V1 target deployables:

1. `api-gateway`
2. `identity-adapter`
3. `customer-service`
4. `kyc-service`
5. `account-service`
6. `ledger-service`
7. `beneficiary-service`
8. `payment-service`
9. `notification-service`
10. `backoffice-service`
11. `audit-service`
12. `statement-service`
13. `imps-network-simulator`

The IMPS simulator is conceptually outside KFG's trust boundary.

---

# 27. Service Ownership

## 27.1 API Gateway

Owns:

- routing
- coarse token validation
- rate limiting
- CORS
- security headers
- request/correlation controls
- request-size limits

Does not own business logic or banking persistence.

## 27.2 Identity Adapter

Owns KFG-to-Keycloak administrative integration such as:

- create/disable customer identity
- create/disable employee identity
- session termination
- permission/role synchronisation
- controlled MFA administrative workflows

Does not own passwords/MFA secrets.

## 27.3 Customer Service

Owns:

- customer profile
- customer number
- addresses
- contact data
- customer lifecycle
- communication preferences
- encrypted identity metadata

Does not own financial accounts, balances, payments or KYC document binaries.

## 27.4 KYC Service

Owns:

- KYC cases
- verification results
- KYC review lifecycle
- document metadata
- officer workflow
- KYC override initiation

## 27.5 Account Service

Owns:

- account metadata
- account number
- customer/account relationship
- account status/restrictions
- account lifecycle
- savings product metadata

Does not independently own writable financial balances.

## 27.6 Ledger Service

Owns:

- ledger accounts
- journals
- journal entries
- holds
- authoritative financial balances
- reversals
- adjustments
- interest postings
- posting invariants

## 27.7 Beneficiary Service

Owns:

- beneficiary data
- beneficiary lifecycle
- cooling period
- verification result
- transfer eligibility data

## 27.8 Payment Service

Owns:

- payment workflow
- idempotency
- transfer validation/orchestration
- payment states
- external IMPS reference
- reconciliation workflow

Does not write ledger tables.

## 27.9 Notification Service

Owns:

- notification requests
- templates
- delivery attempts
- channel status
- in-app notification persistence

## 27.10 Backoffice Service

Owns:

- employee domain profiles
- business roles/permissions
- cases
- notes
- SLA/work queues
- approvals/decisions
- operational dashboards

## 27.11 Audit Service

Owns append-only searchable audit storage.

## 27.12 Statement Service

Owns statement generation requests, generated artifacts and statement-oriented projections/read models.

---

# 28. Database Ownership

Each service owns its own logical PostgreSQL database or schema boundary with dedicated credentials.

Suggested logical databases:

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

- no cross-service SQL joins
- no service reads another service's tables directly
- no cross-service foreign keys
- each service owns its Flyway migrations
- runtime users use least privilege
- applications do not run as PostgreSQL superuser

Locally, one PostgreSQL cluster may host all logical databases.

---

# 29. Data Architecture Standards

- Internal primary keys: UUID
- Business/customer-facing references: separate immutable identifiers
- Database naming: `snake_case`
- Absolute timestamps: PostgreSQL `TIMESTAMPTZ`, Java `Instant`
- Date-only values such as DOB: PostgreSQL `DATE`, Java `LocalDate`
- Currency: explicit 3-character code; V1 `INR`
- Money: `NUMERIC(19,4)` or approved equivalent
- Nullability: `NOT NULL` by default unless absence is meaningful
- Enum persistence: explicit text/code, never ordinal
- Foreign keys: within a service boundary where appropriate
- Destructive cascading deletes: forbidden for financial/history domains
- JSONB: limited to flexible/historical metadata, event payloads and approval snapshots
- Core searchable business fields remain relational

---

# 30. Sensitive Data Architecture

## 30.1 Classification

Example classes:

- Public
- Internal
- Confidential
- Highly Sensitive

Highly sensitive examples:

- PAN-like identifier
- Aadhaar-like identifier
- KYC documents
- authentication recovery material

## 30.2 Field encryption

Highly sensitive identifiers use application-level encryption.

Recommended pattern:

- encrypted value
- HMAC/deterministic lookup token for equality/uniqueness lookup
- masked last-four representation
- encryption-key version

Encryption keys must not be stored alongside ciphertext in the same database.

## 30.3 Key rotation

Encrypted records must support future rotation through key-version metadata.

---

# 31. Kafka and Event Requirements

## 31.1 Purpose

Kafka is used for asynchronous integration such as:

- notifications
- audit event propagation
- read projections
- asynchronous workflow side effects

Kafka must not be required to atomically complete an internal ledger transfer.

## 31.2 Mode

Use modern Kafka in KRaft mode for local infrastructure.

## 31.3 Schema

Use Avro with Schema Registry for production-grade event contracts.

Schema evolution must follow backward-compatible rules.

## 31.4 Event envelope

Standard event metadata includes:

- `eventId`
- `eventType`
- `eventVersion`
- `occurredAt`
- `producer`
- `correlationId`
- `causationId` where appropriate
- payload

## 31.5 Delivery semantics

Assume at-least-once delivery.

Consumers must be idempotent.

## 31.6 Retry/DLQ

Poison events must use bounded retry and dead-letter handling rather than infinite retries.

## 31.7 Privacy

Events must contain only data needed by intended consumers. Highly sensitive KYC identity data must not be broadly broadcast.

---

# 32. Transactional Outbox Requirements

Services that mutate business state and publish events must use the Transactional Outbox Pattern where consistency matters.

Business changes and outbox records are committed in the same local database transaction.

Initial publisher approach:

- polling publisher
- safe concurrent claiming, e.g. `FOR UPDATE SKIP LOCKED`
- bounded retry
- pending/failed metrics

Debezium CDC may be evaluated later.

---

# 33. Consumer Inbox / Idempotency Requirements

Important Kafka consumers maintain an inbox/processed-event record keyed by event ID and consumer identity.

Where a consumed event causes a database mutation, inbox recording and business mutation should occur within the same local transaction where practical.

Duplicate event delivery must not produce duplicate notification, adjustment, projection or other side effects.

---

# 34. Synchronous Integration Requirements

Use REST/HTTP for synchronous service-to-service interactions in V1.

Preferred Spring clients:

- Spring HTTP Interfaces / RestClient
- WebClient only where non-blocking/reactive behaviour is genuinely useful

`RestTemplate` is not the preferred new-client API.

Each dependency must define:

- connection timeout
- read/response timeout
- retry policy
- error mapping
- authentication method

No hidden indefinite defaults.

---

# 35. API Gateway Requirements

Use Spring Cloud Gateway initially.

Responsibilities:

- routing
- JWT validation
- request correlation ID
- rate limiting
- CORS
- security headers
- request-size limits
- access logging

The gateway must not become a business-logic service.

Internal services remain private and are not normally directly internet-accessible.

---

# 36. API Design Standards

## 36.1 Versioning

Use `/api/v1/...` for frontend/public service contracts.

## 36.2 Resource naming

Prefer nouns:

- `POST /api/v1/beneficiaries`
- `GET /api/v1/accounts/{id}`
- `POST /api/v1/payments`

Avoid RPC-style names like `/doPayment` where a resource/action model is clearer.

## 36.3 Command-style domain actions

When an operation is not natural CRUD, use explicit sub-resources/actions such as:

- freeze requests
- approval decisions
- reconciliation requests

## 36.4 Status codes

Use consistent HTTP semantics including:

- 200
- 201
- 202
- 204
- 400
- 401
- 403
- 404
- 409
- 422 where adopted consistently for business validation
- 429
- 500
- 503

## 36.5 Successful response body

Do not wrap every response in generic `{success,data}` envelopes.

Return actual resources/results.

## 36.6 Error contract

All services use a common conceptual error shape, for example:

```json
{
  "code": "INSUFFICIENT_FUNDS",
  "message": "Available balance is insufficient",
  "correlationId": "...",
  "timestamp": "...",
  "fieldErrors": []
}
```

Frontend logic must depend on stable error codes, not parsed error messages.

## 36.7 Pagination

All potentially large collections are paginated.

Default and maximum page sizes are configurable; clients may not request unbounded page sizes.

Transaction timelines may later adopt cursor/keyset pagination.

## 36.8 OpenAPI

Every HTTP service publishes/produces an OpenAPI 3 contract.

---

# 37. Spring Boot Coding Standards

## 37.1 Runtime

- Java 25
- latest stable Spring Boot compatible with Java 25 at initial project bootstrap
- exact selected Spring Boot version pinned in the parent POM
- Maven Wrapper committed

## 37.2 Package structure

Prefer package-by-feature/domain rather than giant global `controller`, `service`, `repository`, `dto` folders.

Typical feature layering:

- API
- application
- domain
- infrastructure

Use architecture pragmatically rather than ceremonially.

## 37.3 Controllers

Controllers remain thin and delegate business behaviour to application/domain services.

## 37.4 DTOs

JPA entities must never be returned directly.

Use explicit request/response/event models. Java records are preferred for immutable transport data where appropriate.

## 37.5 Domain model

Behaviour-rich lifecycle entities such as Payment, Beneficiary and ApprovalRequest may remain normal classes rather than records.

## 37.6 Mapping

Use MapStruct when mapping is repetitive and mechanical. Simple mappings may remain explicit Java.

## 37.7 Lombok

Use sparingly. Avoid `@Data` on domain/JPA entities, especially where it could generate dangerous setters, `equals`, `hashCode` or sensitive `toString` output.

## 37.8 Dependency injection

Constructor injection only.

## 37.9 Validation

Use Jakarta Bean Validation for boundary/structural checks and domain logic for business rules.

Nested inputs require explicit `@Valid` where applicable.

## 37.10 Transactions

Place `@Transactional` on meaningful application/business transaction boundaries.

Transactions must be short and must not remain open while waiting on slow external networks without exceptional justification.

## 37.11 JPA

- Flyway owns schema creation/evolution
- Hibernate `ddl-auto=validate`
- `spring.jpa.open-in-view=false`
- lazy associations by default where appropriate
- review N+1 behaviour
- database constraints are part of correctness

## 37.12 Time

Use injected `Clock` for domain logic that depends on current time.

Avoid scattering `Instant.now()` through business code.

## 37.13 Logging

Use structured, safe logging. Never dump full sensitive request/response bodies.

## 37.14 Domain language

Prefer names such as:

- `postJournal`
- `activateBeneficiary`
- `reconcilePayment`
- `requestAdjustment`

Avoid vague names such as `processData`.

---

# 38. Security Requirements

## 38.1 Transport

- HTTPS externally
- TLS for internal production-like communication
- mTLS considered later during hardening

## 38.2 Defence in depth

Gateway token validation does not replace service-side validation/authorisation.

Every protected service must enforce its own access rules.

## 38.3 Resource ownership / BOLA

Every customer-owned resource access must verify ownership.

A user changing an account ID in the URL must never expose another customer's information.

Explicit tests are required for accounts, payments, beneficiaries, statements and documents.

## 38.4 Rate limiting

Endpoint-specific rate limits use appropriate dimensions such as:

- IP
- customer ID
- employee ID
- account ID
- endpoint

Financial/business limits remain separate from technical rate limits.

## 38.5 CORS

Authenticated APIs must use explicitly configured origins. Wildcard origins are not permitted for authenticated flows.

## 38.6 CSRF

Spring Security CSRF configuration must reflect the actual authentication architecture rather than being disabled blindly.

## 38.7 Input validation

Validate:

- lengths
- formats
- allowed values
- money precision
- date ranges
- pagination limits
- file upload constraints

Frontend validation is never authoritative.

## 38.8 SQL injection

Use JPA/parameter binding/approved query techniques. Never concatenate untrusted values into SQL.

## 38.9 Sensitive logging

Never log:

- passwords
- access tokens
- refresh tokens
- Authorization headers
- OTPs
- TOTP secrets
- full PAN/Aadhaar-like values
- KYC document bodies

## 38.10 File upload hardening

KYC upload handling validates:

- size
- declared content type
- extension
- magic/content bytes
- safe generated object key
- non-executable serving
- malware-scan simulation/hook

## 38.11 Security headers

Gateway/frontends use appropriate policies such as:

- HSTS
- Content-Security-Policy
- X-Content-Type-Options
- Referrer-Policy
- Permissions-Policy

## 38.12 Secrets

Secrets must not appear in source control, Dockerfiles or committed configuration.

Initial approach:

- local ignored environment/secrets
- GitHub Actions secrets
- Kubernetes Secrets for production-like deployment

Future secret manager/Vault may be added during hardening.

## 38.13 Database credentials

Each service has dedicated runtime database credentials with least privilege.

## 38.14 Security events

Generate security events for at least:

- failed login spike
- account lockout
- MFA reset/change
- password change
- new device login
- employee role change
- PII reveal
- account freeze
- suspicious refresh-token reuse

---

# 39. Reliability and Resilience Requirements

## 39.1 Core philosophy

Dependencies will fail. Networks will time out. Events will duplicate. Services will restart.

Financial correctness must survive those failures.

For financial writes, correctness takes priority over availability.

## 39.2 Timeouts

All remote calls require explicit configurable timeouts.

## 39.3 Retries

Use retries only when the complete operation is known to be safe/idempotent.

Read/status queries may retry with bounded exponential backoff and jitter.

Do not blindly retry an uncertain money movement.

## 39.4 Circuit breakers

Use selectively around unstable external dependencies such as:

- IMPS simulator/network
- KYC provider
- notification providers

Do not wrap every internal method in circuit breakers.

## 39.5 Bulkheads

Bound execution resources for expensive/unreliable workloads such as:

- IMPS calls
- statement generation
- notification delivery
- KYC processing

## 39.6 Database outage

If the authoritative financial database is unavailable, financial operations fail safely. Never fall back to Redis or local memory for balance truth.

## 39.7 Redis outage

Redis failure may degrade caches/rate-limiting convenience state, but must not corrupt financial truth.

Security controls that depend critically on Redis must fail safely rather than silently bypassing protection.

## 39.8 Kafka outage

Business/financial transactions that already committed retain their outbox rows. Publishing resumes when Kafka returns.

## 39.9 Keycloak outage

Already issued locally verifiable tokens may continue until expiry where architecture permits. New login/refresh/MFA operations may become unavailable.

No backup custom authentication system is allowed.

## 39.10 Notification outage

A successful payment stays successful. Notification delivery enters retry/failure workflow independently.

## 39.11 Backpressure

Use bounded:

- queues
- thread pools
- HTTP pools
- DB pools
- Kafka consumer concurrency
- upload sizes

Prefer controlled rejection/deferment over unbounded memory/thread exhaustion.

## 39.12 Graceful shutdown

Services must stop accepting traffic before shutdown and allow safe in-flight completion within defined limits.

## 39.13 Health

Distinguish:

- liveness
- readiness

A JVM being alive does not imply the service is ready.

## 39.14 Distributed transaction policy

Do not use XA/2PC across services.

Use:

- local ACID transactions
- outbox
- idempotency
- explicit orchestration
- compensating transactions
- reconciliation

## 39.15 Compensation

Financial compensation is a new immutable financial event, never deletion/rewriting of original history.

## 39.16 Disaster recovery

Define RPO/RTO for environments. Financial databases receive the strictest targets.

Do not claim unrealistic zero-RPO/zero-RTO guarantees on a single developer machine.

## 39.17 Backups

Automate backup and test restore for critical PostgreSQL and object storage data in the production-like environment.

A backup is not considered proven until restored and validated.

---

# 40. Observability Requirements

## 40.1 Tracing

Use OpenTelemetry for distributed tracing.

Trace synchronous service calls and propagate context into asynchronous processing where appropriate.

## 40.2 Correlation vs trace IDs

Keep both concepts:

- trace ID: observability infrastructure
- correlation ID: KFG request/business correlation

## 40.3 Metrics

Use Micrometer and Prometheus-compatible metrics.

Collect:

- JVM
- HTTP
- database pool
- Kafka
- dependency latency/failure
- custom business metrics

## 40.4 Business metrics

Track at least:

- payments initiated/completed/failed/unknown
- IMPS outcome rates
- reconciliation backlog
- oldest unresolved payment age
- KYC backlog
- pending approvals
- outbox backlog
- DLQ count

## 40.5 Metric cardinality

Do not use high-cardinality labels such as customer ID, account ID or transaction ID in Prometheus metrics.

Use logs/traces for individual identities.

## 40.6 Dashboards

Grafana dashboards should cover:

- platform health
- payments
- ledger
- Kafka
- databases
- JVMs
- KYC
- back office
- security events

## 40.7 Centralised logs

Production-like deployments use structured centralised logging.

## 40.8 Alerts

Alerts should represent actionable conditions such as:

- ledger imbalance
- duplicate financial posting
- reconciliation divergence/backlog
- outbox backlog age
- Kafka consumer lag
- DLQ growth
- DB connection exhaustion
- Keycloak outage

## 40.9 Runbooks

Important alerts require documented response procedures.

---

# 41. Testing Strategy

KFG testing depth is risk-driven, not coverage-percentage-driven.

Financial correctness requires overlapping test layers.

## 41.1 Unit/domain tests

Use JUnit 5 and AssertJ.

Pure business logic should be testable without Spring where practical.

Mockito is used only for genuine unit boundaries, not to mock half the application.

## 41.2 Property-based testing

Use jqwik or equivalent for critical invariants such as:

- total debits equal total credits
- successful transfers conserve value across involved customer accounts/system accounts according to posting rules
- successful savings transfer never results in negative available balance
- reversal preserves original history
- amount precision rules

## 41.3 Ledger invariant tests

Mandatory coverage includes:

- balanced journal
- immutable posted entries
- valid journal-entry relationships
- no duplicate posting for the same financial instruction
- correct reversal behaviour
- correct balance projection

## 41.4 Concurrency tests

Use real PostgreSQL.

Examples:

- many simultaneous debits against one account
- opposite-direction transfers to test lock ordering
- concurrent reuse of the same idempotency key
- concurrent maker-checker execution attempts

## 41.5 Database testing

Use PostgreSQL Testcontainers, not H2, for meaningful database integration tests.

Test:

- Flyway migrations
- locks
- constraints
- custom queries
- pagination
- JSONB behaviour where relevant
- optimistic/pessimistic concurrency

## 41.6 Spring integration tests

Use `@SpringBootTest` selectively for important service-level use cases with real infrastructure.

HTTP-facing integration tests use RestAssured.

Tests must assert resulting system/financial state, not only HTTP status codes.

## 41.7 External dependency simulation

Use WireMock for component-level simulation of:

- IMPS network
- KYC provider
- email provider
- SMS provider

Support timeout, error, malformed, delayed and duplicate-response scenarios.

## 41.8 IMPS simulator

Distributed E2E uses the actual independent IMPS simulator service.

WireMock and IMPS simulator serve different test levels.

## 41.9 Kafka integration tests

Use real containerised Kafka for important integration tests.

Test:

- Avro serialization/deserialization
- event production/consumption
- consumer idempotency
- retry/DLQ
- schema compatibility

## 41.10 Outbox tests

Test crash/failure windows such as:

- database commit succeeds while Kafka is down
- application restarts before publish
- event is published and consumer crashes before offset commit

System must recover without duplicate business effects.

## 41.11 Contract testing

Use Spring Cloud Contract by default for important synchronous internal contracts unless later evidence favours Pact.

Kafka schema compatibility must be tested in CI.

## 41.12 Keycloak/security tests

Test real token/realm behaviour for:

- valid customer
- valid employee
- expired token
- wrong issuer
- wrong audience
- wrong realm
- missing authority
- disabled identities

## 41.13 Authorisation matrix tests

Systematically test allowed and denied operations for each employee/customer authority.

## 41.14 BOLA/IDOR tests

Explicitly attempt cross-customer access for:

- accounts
- beneficiaries
- payments
- statements
- documents

## 41.15 Maker-checker tests

Test:

- maker cannot approve own request
- unauthorised checker rejected
- expired request rejected
- rejected request cannot execute
- approved snapshot is exact
- duplicate approval/execution does not double-apply

## 41.16 State-machine tests

Validate allowed and forbidden transitions for:

- customer
- account
- KYC
- beneficiary
- payment
- approval
- support case

## 41.17 Money boundary tests

Include:

- zero
- negative
- exact available balance
- one paise short
- maximum limit
- over limit
- invalid fractional precision
- large BigDecimal values

## 41.18 Time tests

Use injected `Clock` for:

- cooling period
- approval expiry
- SLA breach
- interest calculation
- reconciliation aging

Avoid sleep-based tests.

Use Awaitility for eventual asynchronous conditions.

## 41.19 Mutation testing

Use PIT for critical domains such as:

- ledger
- payment limits
- reconciliation
- approvals
- interest

Mutation testing is not required for trivial DTOs/getters.

## 41.20 Architecture tests

Use ArchUnit for rules such as:

- controller must not access repository directly
- services do not import another service's persistence entities
- domain layers do not depend unnecessarily on Spring/infrastructure

## 41.21 Performance testing

Use Gatling or equivalent for:

- login
- balance retrieval
- transaction history
- internal transfer
- IMPS submission
- back-office search

Measure:

- p50/p95/p99
- throughput
- failure rate
- DB saturation
- Kafka lag
- CPU/memory

After load, verify financial invariants.

## 41.22 Soak/spike testing

Use dedicated environments for prolonged load and spikes to find:

- leaks
- thread growth
- connection exhaustion
- lag accumulation
- degradation

## 41.23 Chaos/fault testing

Use controlled failure injection such as Toxiproxy and service/container restarts to test:

- network latency/disconnect
- Kafka outage
- Redis outage
- PostgreSQL outage
- IMPS delay/failure
- service restart

## 41.24 Security testing

Automate cases for:

- SQL injection attempts
- stored XSS payloads
- oversized requests
- malicious uploads
- token tampering
- privilege escalation
- rate-limit abuse
- BOLA/IDOR
- CORS misconfiguration

Use OWASP ZAP later against deployed test environments.

## 41.25 Recovery testing

Test recovery, not only failure.

Examples:

- Kafka outage causes outbox backlog, Kafka returns, backlog drains once
- service restarts with pending reconciliation
- database restore passes financial consistency checks

## 41.26 E2E golden journeys

Keep E2E suite focused on major system journeys:

1. onboarding -> KYC -> account -> beneficiary -> internal transfer -> notification -> statement
2. IMPS success
3. IMPS timeout -> UNKNOWN -> reconciliation -> resolved result
4. manual adjustment -> maker -> checker -> ledger -> audit -> notification

Do not push every permutation into E2E.

## 41.27 Test data

Tests generate isolated synthetic data through builders/factories/UUIDs.

No globally shared mutable test customer.

## 41.28 CI test tiers

Pull request:

- compile
- unit/domain
- integration/component
- architecture
- contracts
- security basics
- static analysis

Main branch adds broader distributed tests and E2E.

Scheduled/release adds:

- mutation
- performance
- DAST
- chaos
- soak
- backup/restore

---

# 42. Frontend Requirements

Two separate frontend applications:

1. `customer-web`
2. `backoffice-web`

The frontend may be generated using AI-assisted tooling, but backend OpenAPI contracts and this requirements document are authoritative.

Frontend validation supplements but never replaces backend validation.

The frontend communicates through the API Gateway.

No BFF is required initially; introduce one only if real aggregation/security needs justify it.

---

# 43. Local Development Environment

## 43.1 Authoritative developer machine baseline

Primary development machine:

- ASUS ExpertCenter P470VA/V470VA desktop
- Windows 11 Home Single Language
- Intel Core i5-13420H
- 8 physical cores / 12 logical processors
- 16 GB RAM
- UEFI
- Secure Boot enabled
- India Standard Time

This supersedes earlier mistaken i7/14th-generation assumptions.

## 43.2 Local platform approach

Recommended:

- Windows 11 host
- WSL2
- Ubuntu LTS or comparable mainstream WSL Linux distribution
- Docker-based Linux infrastructure
- IntelliJ IDEA for active Spring Boot development

## 43.3 Resource strategy

16 GB RAM is the primary local constraint.

Do not run the entire distributed platform continuously.

Use the smallest realistic service slice for active work.

Recommended starting WSL/Docker budget:

- roughly 6-8 GB memory
- roughly 6-8 logical processors
- swap enabled

Tune based on observed use.

## 43.4 JVM sizing

Local Spring services should begin with conservative heap limits, often approximately 384-512 MB maximum for ordinary services, then be tuned by measurement.

## 43.5 Compose profiles

Use profiles such as:

- `core`
- `payments`
- `onboarding`
- `backoffice`
- `notifications`
- `observability`
- `full`

Infrastructure should run in containers; actively developed Spring services normally run from IntelliJ/JVM for fast debugging.

## 43.6 Heavy tests

Performance, soak, broad chaos and whole-platform tests belong in CI/staging/dedicated environments rather than the normal 16 GB local workflow.

---

# 44. Repository Strategy

Use a monorepo initially because KFG has one primary developer and many coordinated services.

Proposed structure:

```text
kachiguda-financial-group/
├── pom.xml
├── services/
│   ├── api-gateway/
│   ├── identity-adapter/
│   ├── customer-service/
│   ├── kyc-service/
│   ├── account-service/
│   ├── ledger-service/
│   ├── beneficiary-service/
│   ├── payment-service/
│   ├── notification-service/
│   ├── backoffice-service/
│   ├── audit-service/
│   └── statement-service/
├── simulators/
│   └── imps-network/
├── contracts/
│   ├── avro/
│   └── openapi/
├── platform/
│   ├── docker/
│   ├── keycloak/
│   ├── kafka/
│   ├── observability/
│   └── kubernetes/
├── testing/
│   ├── integration/
│   ├── e2e/
│   ├── performance/
│   └── security/
├── docs/
│   ├── requirements/
│   ├── architecture/
│   ├── adr/
│   └── runbooks/
└── scripts/
```

Each service remains independently buildable/deployable and owns its database/migrations/tests/Dockerfile.

---

# 45. Build and Dependency Management

- Maven
- Maven Wrapper committed
- root parent POM for aligned plugin/dependency policy
- Spring Boot BOM/dependency management
- Maven Enforcer where useful
- exact Java version enforced
- exact Spring Boot version pinned after bootstrap selection
- reproducible builds

Avoid large shared `kfg-common` business libraries.

Small shared technical libraries are permitted only when justified, e.g. event envelope or observability conventions.

Domain entities/models must remain service-owned.

---

# 46. Git Workflow

- `main` stays healthy
- feature/bugfix branches
- pull request with CI
- merge to main
- no heavyweight GitFlow by default

Suggested commit style:

- `feat(payment): ...`
- `fix(ledger): ...`
- `test(payment): ...`
- `docs(adr): ...`

Release tags use semantic-style versions such as `v0.1.0`, `v1.0.0`.

---

# 47. Local Infrastructure Bootstrap

Local setup must be reproducible from repository code/configuration.

Avoid instructions requiring manual DBeaver or Keycloak setup.

## 47.1 PostgreSQL

Bootstrap logical databases/users; tables are created only by service-owned Flyway migrations.

## 47.2 Keycloak

Realm/client/role/dev-user configuration must be reproducible through import/config/scripts.

Use synthetic development identities only.

## 47.3 Kafka

Topics and critical settings must be reproducibly created.

Local partition counts remain modest.

## 47.4 MinIO

MinIO is started only when onboarding/KYC work requires it.

## 47.5 Observability

Prometheus/Grafana/OTel/logging stack should be optional local profiles rather than permanently running services.

---

# 48. CI/CD Requirements

Use GitHub Actions initially.

## 48.1 Pull request CI

Expected stages:

1. checkout
2. Java 25 setup
3. dependency cache
4. compile
5. unit/domain tests
6. integration/Testcontainers tests
7. architecture tests
8. contract tests
9. static analysis
10. coverage/reporting
11. secret scanning
12. dependency/container security scanning where applicable

## 48.2 Main/release CI

Add:

- broader distributed integration tests
- E2E golden journeys
- Docker image build
- container scan
- image publish

## 48.3 Heavy scheduled/release CI

Add:

- PIT mutation testing
- performance
- OWASP ZAP/DAST
- chaos
- soak
- backup/restore validation

## 48.4 Container registry

GitHub Container Registry is acceptable initially.

Deployment uses immutable version/tag/digest rather than `latest`.

## 48.5 Environment promotion

Promote the same built image through:

- staging
- production-like

Do not rebuild different binaries per environment.

---

# 49. Deployment Requirements

Progression:

1. IntelliJ + Docker dependencies
2. full Docker Compose
3. local Kubernetes for deployment validation
4. cloud-hosted staging/production-like environment

Kubernetes is not the default daily development platform on the 16 GB machine.

Production-like Kubernetes requirements eventually include:

- readiness/liveness probes
- resource requests/limits
- graceful shutdown
- network policies
- RBAC
- secrets
- environment separation
- non-root containers
- controlled actuator exposure

Helm is the preferred initial packaging candidate once Kubernetes deployment begins.

---

# 50. Container Requirements

Every Spring service image should use:

- multi-stage build
- minimal runtime image
- non-root user
- no secrets in image layers
- sensible JVM container memory settings
- health/readiness integration
- resource limits in deployment environments

Read-only filesystem and dropped capabilities should be used where practical.

---

# 51. Database Migration Requirements

Use Flyway.

Rules:

- migrations are forward-only
- never edit a migration already deployed to a shared environment
- schema changes are small/reviewable
- destructive changes use expand/migrate/contract where rolling compatibility matters
- large data backfills are not forced into startup migrations
- CI tests empty-to-latest migrations
- later release CI also tests previous-version-to-latest upgrades

Hibernate validates mappings but does not own production schema evolution.

---

# 52. Operational Data and Seed Requirements

Local/test seed data uses synthetic scenarios only.

Example data:

- active customer with funded savings account
- second active customer
- frozen customer
- active internal beneficiary
- external beneficiary in cooling period

Opening/test funds must enter through a controlled ledger funding transaction rather than direct balance SQL updates.

Local convenience utilities must never bypass ledger invariants and must not exist in production profiles.

---

# 53. Production Readiness Standard

A KFG service is not considered production-ready until it has, where relevant:

- health checks
- metrics
- structured logging
- tracing
- timeouts
- resilience behaviour
- secure authentication/authorisation
- database migrations
- integration tests
- contract tests
- load expectations
- runbook
- alerts
- backup/recovery consideration
- documented failure behaviour

"Works on localhost" is not the V1 definition of production readiness.

---

# 54. Architecture Principles

The following principles are binding for V1:

1. **The ledger is financial truth.**
2. **A posted financial fact is never rewritten.**
3. **A balance is a projection of ledger truth, not a freely editable field.**
4. **Internal KFG money movement is atomic inside Ledger Service.**
5. **Kafka is not inserted into the internal ACID money path.**
6. **External-payment ambiguity is represented explicitly as UNKNOWN and reconciled.**
7. **Idempotency is mandatory for money-moving requests.**
8. **At-least-once event delivery requires idempotent consumers.**
9. **Services own their data; no cross-service table access.**
10. **Authenticate explicitly and authorise every resource.**
11. **Employee privileges never bypass audit or ledger invariants.**
12. **Dangerous actions are explicit, permissioned and often maker-checker controlled.**
13. **PII and secrets are minimised, encrypted/masked and never casually logged.**
14. **Failures must not silently become incorrect financial outcomes.**
15. **A timeout is not the same thing as a financial failure.**
16. **Testing validates financial state and invariants, not just API status codes.**
17. **Clarity beats cleverness.**
18. **Microservices are used for justified bounded contexts, not architecture theatre.**

---

# 55. Initial Architecture Decision Records

The repository should create ADRs for at least:

- ADR-001: Microservice architecture for KFG V1
- ADR-002: Monorepo with independently deployable services
- ADR-003: PostgreSQL database ownership per service
- ADR-004: Keycloak owns authentication and MFA
- ADR-005: Ledger Service owns authoritative financial balances
- ADR-006: Kafka + Avro + Schema Registry
- ADR-007: Transactional Outbox Pattern
- ADR-008: Internal transfers use one Ledger Service ACID boundary
- ADR-009: IMPS timeout produces UNKNOWN and reconciliation
- ADR-010: Testcontainers with real PostgreSQL/Kafka
- ADR-011: No Eureka; use Compose/Kubernetes discovery
- ADR-012: No Spring Cloud Config Server initially
- ADR-013: No full event sourcing
- ADR-014: No workflow engine initially
- ADR-015: Java 25 and pinned stable Spring Boot at bootstrap

---

# 56. Golden End-to-End Acceptance Journeys

## 56.1 Customer onboarding and internal transfer

1. Customer registers
2. Identity created in Keycloak
3. Email/mobile verification completes
4. Profile completed
5. KYC submitted
6. Simulated verification passes or employee approves
7. Customer becomes active
8. Savings account automatically created
9. Controlled test funding journal credits account
10. Customer creates internal beneficiary
11. Beneficiary cooling period completes
12. Customer performs step-up confirmation
13. Internal transfer completes atomically
14. Ledger journal balances
15. Source/destination balances are correct
16. Payment event is emitted through outbox
17. Notification delivered
18. Transaction appears in history
19. Statement includes the transaction

## 56.2 IMPS success

1. Customer creates external beneficiary
2. Beneficiary becomes active
3. Customer submits IMPS payment with idempotency key
4. Payment enters processing
5. Financial posting/hold logic executes according to design
6. IMPS simulator returns success
7. KFG completes payment state
8. Ledger remains balanced
9. Customer is notified
10. Transaction appears in history

## 56.3 IMPS ambiguity and reconciliation

1. Customer submits IMPS payment
2. External request times out
3. KFG records `UNKNOWN`
4. KFG does not blindly retry the payment
5. Reconciliation queries external authoritative state
6. Outcome resolves to success or failure
7. If failure, controlled reversal/release is posted
8. All journals remain immutable and balanced
9. Customer/back-office status reflects final result

## 56.4 Back-office manual adjustment

1. Customer support/operations creates a case
2. Operations maker creates adjustment request
3. Request snapshot is immutable
4. Independent authorised checker approves
5. Controlled ledger adjustment is posted
6. No direct balance update occurs
7. Audit trail records the full workflow
8. Customer receives required notification
9. Case is resolved

---

# 57. V1 Definition of Done

KFG V1 is complete when:

> An individual customer can securely onboard, pass simulated KYC, obtain a savings account, manage beneficiaries, execute reliable internal and simulated IMPS transfers backed by an immutable double-entry ledger, view transaction history and statements, and receive required notifications; while authorised employees can operate the bank through controlled KYC, support, account restriction, reconciliation, maker-checker and financial-adjustment workflows, with security, auditability, resilience, observability, automated testing and repeatable deployment demonstrated across the platform.

In addition, V1 completion requires:

- security controls implemented and tested
- ledger property/invariant tests
- concurrency tests
- contract tests
- Kafka/outbox/inbox reliability tests
- E2E golden journeys
- performance tests
- basic chaos/fault testing
- central observability
- CI/CD
- Docker Compose deployment
- Kubernetes production-like deployment
- backup and restore demonstration
- operational runbooks
- architecture documentation and ADRs

---

# 58. Recommended Implementation Sequence

This sequence is a roadmap, not permission to weaken the frozen requirements.

## Phase 0 - Repository and Platform Bootstrap

- monorepo
- Java 25
- Spring Boot version selection and pinning
- parent POM
- Maven Wrapper
- formatting/static analysis
- Docker Compose core
- PostgreSQL bootstrap
- Kafka/KRaft + Schema Registry
- Redis
- Keycloak realms/clients
- base CI
- initial ADRs

## Phase 1 - Identity and Customer Foundation

- API Gateway
- identity adapter
- customer service
- customer realm flow
- employee realm foundation
- customer lifecycle
- profile security/encryption

## Phase 2 - KYC

- KYC service
- MinIO
- mock verifier
- review queue
- KYC employee workflow
- account auto-provision trigger after approval

## Phase 3 - Account and Ledger Foundation

- account metadata service
- ledger schema
- ledger accounts
- journal posting
- balance projection
- holds
- funding transaction
- ledger invariant/property tests
- concurrency tests

## Phase 4 - Beneficiaries

- internal/external beneficiary model
- verification
- cooling period
- step-up confirmation
- eligibility API

## Phase 5 - Internal Transfers

- payment service
- idempotency
- transfer limits
- Ledger Service posting integration
- outbox
- notifications
- transaction history

## Phase 6 - IMPS and Reconciliation

- IMPS network simulator
- processing/unknown states
- external timeouts/failures
- reconciliation worker
- reversals
- operational reconciliation view

## Phase 7 - Back Office

- employee domain
- permissions
- support cases
- SLA
- maker-checker engine
- temporary debit block
- unfreeze approval
- manual adjustment

## Phase 8 - Audit, Notifications and Statements

- audit service
- notification service hardening
- in-app notifications
- statement service
- PDF/CSV generation
- statement projection/reconciliation

## Phase 9 - Savings Interest

- configurable rate
- daily accrual calculation
- monthly ledger posting
- idempotent batch tests

## Phase 10 - Production Hardening

- full observability
- alerting/runbooks
- resilience tuning
- security scans
- chaos testing
- performance/soak tests
- backup/restore
- Kubernetes
- production-like CI/CD promotion

---

# 59. Change Control After Requirements Freeze

After approval of this document, new ideas must be classified as one of:

- V1 requirement clarification
- V1 change request
- V2 backlog

Features must not silently enter implementation because they are interesting.

Changes that affect financial invariants, service ownership, security boundaries or data contracts require an ADR and explicit requirements update.

---

# 60. Final Engineering Rule

> **Make illegal financial states difficult to represent, make dangerous operations explicit, preserve immutable financial history, treat external uncertainty honestly, and prove correctness through tests that inspect state, invariants, concurrency and recovery—not merely happy-path HTTP responses.**

