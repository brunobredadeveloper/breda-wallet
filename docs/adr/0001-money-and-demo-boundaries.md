# ADR 0001: Money and simulator boundaries
Status: Accepted

Use Long integer cents for BRL amounts. Parse decimal input with BigDecimal and reject fractional cents and overflow. Floating point rounding is unacceptable for ledger operations.

Domain is plain Kotlin; the data simulator implements its repository contract. Mutex serializes balance checks and ledger updates. A request ID maps to its payload and result: exact retries return the original result; differing payloads conflict.

Idempotency exists only within one repository instance. The UI generates an ID per valid submission and clears the amount after sending. Production retry handling must retain that ID across process recreation and enforce it on the server. A device-side mutex is not a distributed transaction.

Data is intentionally ephemeral. Room, backend integration, authentication and encryption require separate decisions. The demo does not claim banking security or real Pix connectivity.
