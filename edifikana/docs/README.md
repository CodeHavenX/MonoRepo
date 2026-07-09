# Edifikana Back-End — Authentication Reassessment

These documents capture an assessment of the current authentication approach
(the `ContextRetriever` pattern) and propose remediation plans for the weaknesses
found. Each plan is self-contained: problem, evidence, proposal, steps, and validation.

## Context

Edifikana authenticates requests **without** Ktor's `Authentication` plugin. Instead,
a Koin-injected `ContextRetriever<P>` reads the token from the call and returns a
`ClientContext<P>` (authenticated payload or unauthenticated). Each operation opts in
via `handler()` (authenticated) or `unauthenticatedHandler()` (public) in
`framework/core-ktor/.../ControllerUtils.kt`. Today: **84 authenticated ops, 4 public**.

**Overall verdict:** the abstraction is sound and worth keeping. A wholesale migration
to Ktor's `Authentication` plugin is **not** warranted. The plans below fix concrete
defects and offer an *optional* hybrid migration that keeps the retriever seam while
gaining structural route protection.

## Plans (in recommended order)

| # | Plan | Type | Severity | Effort | Status |
|---|------|------|----------|--------|--------|
| 1 | [Context retriever error handling](01-context-retriever-error-handling.md) | Correctness bug | High | Small | ✅ Done |
| 2 | [Remove dead `handleCall` path](02-remove-dead-handlecall-path.md) | Cleanup | Medium | Small | ✅ Done |
| 3 | [Structural route authentication (hybrid)](03-structural-route-authentication.md) | Architecture | Medium | Large | Backlog |
| 4 | [Local JWT verification](04-local-jwt-verification.md) | Performance | Medium | Medium | Backlog |
| 5 | [OpenAPI security inference](05-openapi-security-inference.md) | Maintainability | Low | — (folded into #3) | Backlog |

> Plans 1 and 2 were implemented together (they share the auth request-handling path in
> `framework/core-ktor`). The same token-validation fix from Plan 1 was also applied to
> `FlyerBoardContextRetriever`.

**Suggested sequencing:** do 1 and 2 first (small, unambiguous, no architecture debate).
Evaluate 4 independently as a perf win. Treat 3 (which subsumes 5) as a deliberate,
optional refactor if structural safety becomes a priority.
