# Plan 5 — OpenAPI security inference (the Layer 4 tax)

**Type:** Maintainability &nbsp;·&nbsp; **Severity:** Low &nbsp;·&nbsp; **Effort:** — (resolved by Plan 3)

> This is a **symptom**, not an independent defect. Its clean resolution is
> [Plan 3 (structural route authentication)](03-structural-route-authentication.md).
> This document exists so the weakness is tracked explicitly.

## Problem

Because edifikana authenticates **inside the handler** rather than via Ktor's
`authenticate {}` routing gate, Ktor's `routing-openapi` integration cannot infer which
operations are secured. To make the generated OpenAPI docs correct, Layer 4 added bespoke
plumbing:

- an `authenticated: Boolean` flag threaded from `handler()` (true) /
  `unauthenticatedHandler()` (false) through `OperationHandler.handle` → `handleImpl` →
  `rounteDescription` → `describeMetadata`;
- manual emission of `security { requirement("bearerAuth") }` for authenticated ops;
- a one-time `registerJWTSecurityScheme("bearerAuth")` in `configureOpenApiEndpoint`;
- conditional-401 logic driven by that same flag.

This works and is contained, but it is **framework code we maintain by hand** to reproduce
something Ktor would derive automatically from route auth metadata.

## Evidence

- `framework/core-ktor/.../OperationHandler.kt` — `authenticated` parameter threaded
  through `handle`/`handleImpl`/`rounteDescription`; `describeMetadata` emits the security
  requirement conditionally.
- `framework/core-ktor/.../Application.kt` — `registerJWTSecurityScheme(BEARER_SECURITY_SCHEME)`
  and `BEARER_SECURITY_SCHEME = "bearerAuth"`.
- `framework/core-ktor/.../ControllerUtils.kt` — `handler()` passes `authenticated = true`,
  `unauthenticatedHandler()` passes `authenticated = false`.

## Why not fix it standalone

The only way to make security *inferred* (rather than hand-emitted) is to put auth on the
routing tree via `authenticate {}` — which is exactly [Plan 3](03-structural-route-authentication.md).
Any standalone "fix" here would just be more manual emission. So there is no separate work
item: **adopt Plan 3 and this tax disappears.**

## What Plan 3 removes

Once operations register inside `authenticate("bearerAuth") { }` and the provider
registers its scheme:

- `OpenApiDocSource.Routing`'s `findSecuritySchemes()` surfaces the scheme automatically.
- Per-operation security requirements are inferred from route auth metadata — delete the
  manual `security { requirement(...) }` emission.
- The `authenticated` flag threading can be removed; the conditional-401 logic (still
  valid) sources its input from route metadata instead of a hand-passed boolean.

## If Plan 3 is declined

The current Layer 4 approach is an acceptable steady state — it produces correct docs. In
that case, keep it as-is and simply document that the manual threading is intentional
(the cost of not using Ktor's `Authentication` plugin). No action required.
