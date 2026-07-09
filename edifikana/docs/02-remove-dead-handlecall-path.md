# Plan 2 — Remove the dead `handleCall` authentication path

**Type:** Cleanup / duplication &nbsp;·&nbsp; **Severity:** Medium &nbsp;·&nbsp; **Effort:** Small

## Problem

`framework/core-ktor/.../ControllerUtils.kt` contains **two** parallel implementations of
the authenticate-then-dispatch flow:

- **Live path:** `handler()` / `unauthenticatedHandler()` → `operation.handle(...)` →
  `OperationHandler.handleImpl`. This is what all 88 operations use.
- **Dead path:** `handleCall()` / `handleUnauthenticatedCall()` (plus the shared
  `handleCall(verifyClientContext, ...)` overload) — a self-contained copy of the same
  logic (context retrieval, 401 on failure, success/error response mapping).

A repo-wide search finds **zero callers** of `handleCall` / `handleUnauthenticatedCall`
outside their own definitions. It is ~100 lines of duplicated auth-and-dispatch logic
that can silently drift from the real path (e.g. the live path gained the `authenticated`
flag and `ResponsePolicy` enforcement in Layers 3–4; the dead path never did). Dead auth
code is a maintenance hazard and a misleading reference for future contributors.

## Evidence

`framework/core-ktor/.../ControllerUtils.kt`:

- `handleUnauthenticatedCall` — lines ~112–125
- `handleCall` (authenticated convenience overload) — lines ~136–149
- `handleCall` (generic `verifyClientContext` overload) — lines ~161–209

Search result: no references to these symbols anywhere except their declarations.
The live path (`handler`/`unauthenticatedHandler`, lines ~29–101) does not call them —
it delegates to `operation.handle`.

## Proposal

Delete the three dead functions. Keep `requireAuthenticatedClientContext` (lines
~221–234) — it **is** used by the live `handler()` path.

## Steps

1. Re-confirm zero usage immediately before deleting:
   ```bash
   grep -rn "handleUnauthenticatedCall\|handleCall" --include=*.kt . \
     | grep -v "ControllerUtils.kt"
   ```
   Expect no results.
2. Remove `handleUnauthenticatedCall`, both `handleCall` overloads, and any imports left
   unused after their removal (e.g. `respondBytes`, `respondNullable`, `logE` — verify
   each is not still referenced by the surviving `handler`/`unauthenticatedHandler`).
3. Keep `requireAuthenticatedClientContext` and its `@Suppress("UseCheckOrError")`.

## Testing / validation

- `./gradlew :framework:core-ktor:release --quiet` — compilation + detekt + existing
  tests must stay green.
- Run the edifikana controller test suite to confirm the live path is untouched:
  `./gradlew :edifikana:back-end:release --quiet`.

## Risks

- Very low. The only subtlety is import cleanup — removing an import still referenced by
  the live path will fail compilation, which the release build catches immediately.
- If any *other* consumer of `framework/core-ktor` (flyerboard/runasimi/template) uses
  these helpers, deletion would break them. The selective-sync workspace can't compile
  those, so re-run the `grep` across the full repo checkout (not just the synced modules)
  before deleting, or confirm via CI.
