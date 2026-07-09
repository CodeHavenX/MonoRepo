# Plan 3 — Structural route authentication (hybrid migration)

**Type:** Architecture &nbsp;·&nbsp; **Severity:** Medium &nbsp;·&nbsp; **Effort:** Large &nbsp;·&nbsp; **Optional**

> This plan subsumes [Plan 5 (OpenAPI security inference)](05-openapi-security-inference.md):
> adopting Ktor's `authenticate {}` makes OpenAPI security requirements inferable, removing
> the manual threading added in Layer 4.

## Problem

Authentication protection today is **by developer discipline, not by structure**. An
endpoint is protected only because someone called `handler()` instead of
`unauthenticatedHandler()`. There is no routing-tree-level guarantee: a wrong wrapper
silently ships an unauthenticated endpoint, and nothing about the route tree makes the
protection boundary visible or enforceable.

Consequences:
- **Safety:** a single-word mistake (`unauthenticatedHandler`) opens an endpoint with no
  structural signal. With 84 protected ops, the probability of an eventual slip is real.
- **Framework tax:** because auth runs *inside* the handler rather than as a routing gate,
  Ktor cannot infer OpenAPI security, forcing the bespoke `authenticated` flag +
  manual `security { requirement("bearerAuth") }` emission (Layer 4).

## Non-goal

**Do not rewrite auth from scratch.** The `ContextRetriever<P>` / `ClientContext<P>` seam
is valuable — it keeps the token→payload logic app-agnostic and makes controller tests
trivial to mock. This plan *preserves* that seam.

## Proposal — hybrid: retriever behind a Ktor `bearer {}` provider

Keep `ContextRetriever`/`ClientContext<P>` as the token→payload logic, but invoke it from
**inside** a Ktor `Authentication` provider so protection becomes structural and OpenAPI
security is inferred automatically.

1. Add `ktor-server-auth` (and, if combined with [Plan 4](04-local-jwt-verification.md),
   `ktor-server-auth-jwt`) to `framework/core-ktor`.
2. Install `Authentication` once with a `bearer("bearerAuth")` provider whose
   `authenticate { }` lambda delegates to the injected `ContextRetriever`:
   - retriever returns `AuthenticatedClientContext<P>` → expose the payload as the
     principal (Ktor 3.x allows arbitrary principal types via `call.principal<T>()`).
   - retriever returns unauthenticated → return `null` so Ktor issues the 401 challenge.
   - retriever signals provider-unavailable ([Plan 1](01-context-retriever-error-handling.md))
     → propagate as 503/500, not a 401 challenge.
3. Register the security scheme through the provider so
   `OpenApiDocSource.Routing`'s `findSecuritySchemes()` picks it up (replacing the manual
   `registerJWTSecurityScheme` call if the provider registers it natively).
4. Rework `handler()` to register operations *inside* an `authenticate("bearerAuth") { }`
   block; keep `unauthenticatedHandler()` outside it. The route tree now encodes
   protection structurally.
5. Retrieve the typed payload in handlers via the principal instead of re-running the
   retriever, preserving the `AuthenticatedClientContext<P>` ergonomics.

## Steps

1. **Spike** the `bearer {}` provider delegating to a stub `ContextRetriever` in a
   throwaway test to confirm: (a) principal typing works, (b) `findSecuritySchemes()`
   emits the scheme, (c) per-route `authenticate {}` yields inferred OpenAPI security.
2. Introduce the provider install in `framework/core-ktor` (`Application.kt`).
3. Change `handler()` in `ControllerUtils.kt` to wrap `route` in `authenticate("bearerAuth")`.
4. Remove the now-redundant Layer 4 machinery once inference is confirmed:
   - the `authenticated: Boolean` threading through `handle`/`handleImpl`/`rounteDescription`,
   - the manual `security { requirement(...) }` in `describeMetadata`,
   - the conditional-401 logic can stay (it is still correct), but its `authenticated`
     input can come from route auth metadata instead of a hand-passed flag.
5. Update controller tests: mocking shifts from "mock `ContextRetriever`" to "install a
   test auth provider / inject a principal." Preserve a helper so tests stay concise.
6. Validate the generated spec is unchanged or improved (security requirements now
   inferred) against a captured baseline.

## Testing / validation

- Full `./gradlew :framework:core-ktor:release :edifikana:back-end:release --quiet`.
- A test asserting a protected route returns 401 **without** the handler ever running
  (the gate now rejects before dispatch) — this is the behavior the current design cannot
  guarantee.
- Diff the OpenAPI spec before/after to confirm security requirements are preserved.

## Risks / cost

- **Invasive:** touches the core handler, the registration helpers, and all 88 ops'
  registration path (though ops themselves shouldn't need edits if `handler()` absorbs the
  change).
- **New dependencies:** `ktor-server-auth`(+`-jwt`).
- **Test migration:** every controller test currently mocks `ContextRetriever`; the
  mocking strategy changes. Mitigate with a shared test helper.
- **Cross-app impact:** flyerboard/runasimi/template also use `handler()`; the change is
  framework-level, so verify them (not compilable in the selective-sync workspace).
- Because of the above, this is **optional** and should be scheduled deliberately — justified
  by structural safety, with OpenAPI inference as a bonus, not the sole driver.
