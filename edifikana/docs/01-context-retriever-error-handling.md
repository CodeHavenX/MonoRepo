# Plan 1 — Fix `ContextRetriever` error handling

**Type:** Correctness bug &nbsp;·&nbsp; **Severity:** High &nbsp;·&nbsp; **Effort:** Small

## Problem

`SupabaseContextRetriever` collapses three distinct outcomes into a single
`UnauthenticatedClientContext`, which the handler maps to **401 Unauthorized**:

1. **No token** — legitimately unauthenticated (401 is correct).
2. **Invalid/expired token** — the client *tried* to authenticate and failed (401 is
   defensible, but should be distinguishable from case 1).
3. **Supabase is unreachable / throws** — a *backend dependency outage*. Returning 401
   here is wrong: it tells the client "you are unauthenticated" when the truth is
   "we could not verify you right now." This should surface as **503 Service Unavailable**
   (or 500), not 401.

The bug masks incidents (an auth-provider outage looks like a wave of client auth
failures) and misleads clients into re-logging-in when the fault is server-side.

## Evidence

`edifikana/back-end/.../authentication/SupabaseContextRetriever.kt`:

```kotlin
val token = headerMap[HEADER_TOKEN_AUTH]?.firstOrNull()
if (token.isNullOrBlank()) {
    return ClientContext.UnauthenticatedClientContext()   // case 1: no token
}
val user = try {
    auth.retrieveUser(token)
} catch (e: Exception) {                                   // cases 2 AND 3 collapsed
    logW(TAG, "Error retrieving user...: ${e.message}")
    return ClientContext.UnauthenticatedClientContext()
}
```

The single `catch (e: Exception)` cannot tell an invalid-token (auth) error apart from a
network/5xx (availability) error, and both become the same 401 as "no token at all."

## Proposal

Distinguish **authentication failure** (client's fault → 401) from **verification
unavailability** (server's fault → 503/500). Two options:

### Option A — throw a typed exception for outages (recommended)
Catch narrowly. Map Supabase auth-rejection exceptions to
`UnauthenticatedClientContext`; rethrow (or wrap) transport/5xx exceptions so the
existing `validateClientError` path returns a 5xx.

- The framework already maps non-`ClientRequestException` throwables to 500
  (`validateClientError`). Introduce a dedicated exception (e.g.
  `AuthProviderUnavailableException`) mapped to **503** to be precise, or let it fall
  through to 500 if 503 wiring is undesirable.
- Keep the "no token" and "rejected token" branches returning
  `UnauthenticatedClientContext` → 401.

### Option B — enrich `ClientContext`
Add a third state (e.g. `ClientContext.VerificationUnavailable`) and map it to 503 in
the handler. More invasive (touches the sealed interface and every `when`), so Option A
is preferred unless the extra state is independently useful.

## Steps (Option A)

1. In `SupabaseContextRetriever.getContext`, replace the broad `catch (e: Exception)`
   with discrimination:
   - Supabase auth-rejection (e.g. `RestException` / 401/403 from `retrieveUser`) →
     `return ClientContext.UnauthenticatedClientContext()`.
   - Any other throwable (timeout, connection refused, 5xx) → rethrow as
     `AuthProviderUnavailableException` (new, in `framework/core-ktor` alongside the
     other auth types, or edifikana-side if kept app-specific).
2. If precise 503 is wanted, extend `validateClientError`
   (`framework/core-ktor/.../OperationHandler.kt`) to map the new exception to
   `HttpStatusCode.ServiceUnavailable`. **Otherwise** it already yields 500 with no
   change — acceptable as a first cut.
3. Confirm the `assertNull(auth.currentUserOrNull(), ...)` invariant still holds on the
   error paths (it should; we return before it on failure).

## Testing / validation

- Unit-test `SupabaseContextRetriever` with a mocked `Auth`:
  - no token → `UnauthenticatedClientContext`.
  - `retrieveUser` throws an auth-rejection → `UnauthenticatedClientContext`.
  - `retrieveUser` throws a transport error → `AuthProviderUnavailableException`.
- Add a controller-level test asserting an outage yields **503/500**, not 401.
- `./gradlew :edifikana:back-end:release --quiet` (or `:framework:core-ktor:release`
  if `validateClientError` changes).

## Risks

- Need to confirm which concrete exception types the Supabase `Auth.retrieveUser`
  raises for rejection vs. transport failure; if they are not cleanly separable, fall
  back to inspecting HTTP status on the thrown exception.
- Introducing a 503 path is a **new externally-visible status** — declare it in the
  relevant `ResponsePolicy` if strict response enforcement is enabled for those ops.
