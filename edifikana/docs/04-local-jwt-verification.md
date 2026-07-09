# Plan 4 — Local JWT verification

**Type:** Performance / resilience &nbsp;·&nbsp; **Severity:** Medium &nbsp;·&nbsp; **Effort:** Medium &nbsp;·&nbsp; **Tracking:** [#532](https://github.com/CodeHavenX/MonoRepo/issues/532)

## Problem

`SupabaseContextRetriever` validates every authenticated request with a **remote**
call to Supabase:

```kotlin
val user = auth.retrieveUser(token)   // network round-trip, every request
```

With 84 authenticated operations, this puts a Supabase network round-trip on the hot path
of essentially every API call. There is no local signature verification and no caching.
Effects:

- **Latency:** every request pays a round-trip to the auth provider before any business
  logic runs.
- **Coupling to provider availability:** a Supabase slowdown/outage degrades *all*
  authenticated traffic (see also [Plan 1](01-context-retriever-error-handling.md), which
  ensures such outages at least report correctly).
- **Cost/throughput:** ties request throughput to the auth provider's rate limits.

This is orthogonal to the `ContextRetriever` vs. `Authentication`-plugin question — it is
about *how the token is verified*, not *where the verification is wired in*.

## Proposal

Verify the Supabase JWT **locally** using the project's JWT secret / JWKS, and only fall
back to a remote lookup when strictly necessary.

- Supabase issues signed JWTs. The token's signature, expiry (`exp`), issuer (`iss`), and
  audience (`aud`) can be validated locally without a network call.
- The `sub` claim carries the user id; other needed claims (email, role) are typically in
  the token. This is enough to build `SupabaseContextPayload` for most requests.
- Reserve `auth.retrieveUser` (or a cached variant) for cases that genuinely need
  freshly-fetched user data not present in the token.

### Verification options
1. **Symmetric (HS256) secret** — validate with the Supabase JWT secret. Simplest;
   requires securely provisioning the secret to the backend.
2. **Asymmetric (JWKS)** — if the project uses/rotates asymmetric keys, fetch and cache
   the JWKS and verify against the rotating public keys. More robust to key rotation.

If [Plan 3](03-structural-route-authentication.md) is adopted, `ktor-server-auth-jwt`'s
`jwt {}` provider does most of this out of the box; otherwise verify inside the retriever
using a JWT library.

## Steps

1. Determine the Supabase project's token signing scheme (HS256 secret vs. JWKS) and how
   the secret/JWKS is provisioned to the backend config.
2. Add local verification in `SupabaseContextRetriever` (or the `jwt {}` provider from
   Plan 3): validate signature + `exp`/`iss`/`aud`, extract `sub` → `UserId`.
3. Build `SupabaseContextPayload` from claims. Decide what (if anything) still requires a
   remote `retrieveUser`; if some fields do, add a short-TTL cache keyed by user id.
4. Keep the [Plan 1](01-context-retriever-error-handling.md) distinction: signature/expiry
   failure → 401; inability to *reach* JWKS (when required) → 503/500.
5. Provision the secret/JWKS via the existing settings mechanism
   (`config.properties.*` / `SettingsHolder`), never hard-coded.

## Testing / validation

- Unit tests with hand-crafted tokens: valid, expired, wrong-signature, wrong-issuer,
  wrong-audience → correct classification.
- Verify no network call occurs on the happy path (mock/spy the Supabase `Auth`).
- `./gradlew :edifikana:back-end:release --quiet`.
- Load/latency sanity check: authenticated endpoint latency should drop by roughly one
  auth-provider round-trip.

## Risks

- **Secret management:** the JWT secret is highly sensitive; it must live in secure config,
  not in the repo. JWKS avoids shipping a shared secret.
- **Claim drift:** if Supabase changes token claims/format, local parsing can break;
  cover with tests and pin expected claim names.
- **Revocation:** local verification cannot see server-side revocation before `exp`. If
  immediate revocation matters, keep a lightweight remote/cached check for sensitive ops.
