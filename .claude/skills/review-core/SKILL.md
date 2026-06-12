---
name: review-core
description: "Review framework, architecture, and low-level Kotlin library code for API design, coroutine safety, documentation, and style violations. Use when asked to review framework modules, shared libraries, or cross-cutting infrastructure code."
allowed-tools: Read, Glob, Grep, Bash
---

# review-core — Framework & Core Code Quality Reviewer

## Purpose
Analyze framework, architecture module, and shared library code in a given scope and produce a prioritized list of findings with remediations.

## Step 1 — Resolve scope

Read `.claude/skills/_shared/scope-resolution.md` and follow those instructions.

Restrict the resolved file list to files under `framework/`, `architecture/`, `models/`, and `api/` directories at the repo root level. Skip files that are clearly in `front-end/` feature code or `back-end/` application code.

Read each relevant file fully before analysing.

## Step 2 — Apply rules

Read `.claude/skills/_shared/generic-rules.md` and apply those rules to all files.

For core/framework code, **G1 is elevated to P0**: framework code is consumed by other modules without access to the implementation — undocumented public API is a hard failure. The KDoc must describe the contract, not just restate the name.

Then apply the additional core-specific rules below.

### Coroutine and threading rules

**CORE1 — No direct dispatcher usage** (P0)
`Dispatchers.IO` and `Dispatchers.Main` must never be used directly in framework code. Inject dispatchers via constructor parameters or via the `@BackgroundDispatcher` / `@UIThreadDispatcher` annotations, so callers can substitute test dispatchers.
Remediation: replace `Dispatchers.IO` / `Dispatchers.Main` with an injected `CoroutineDispatcher` parameter.

**CORE2 — No hardcoded threads** (P0)
Do not create `Thread(...)` manually or use `Executors.*` directly. Use coroutines with injected dispatchers.
Remediation: replace with a coroutine launched on the appropriate injected dispatcher.

**CORE3 — Structured concurrency** (P1)
`GlobalScope` is forbidden. All coroutines must be launched within a passed-in `CoroutineScope` or the component's own lifecycle-bound scope.
Remediation: accept a `CoroutineScope` parameter and launch coroutines on it.

### API design rules

**CORE4 — Result type for fallible operations** (P1)
Public functions that can fail must return `Result<T>` rather than throwing exceptions or returning nullable types as error signals.
Remediation: wrap the return type in `Result<T>` and use `Result.success` / `Result.failure`.

**CORE5 — No leaking implementation types** (P1)
Public API must expose interfaces or abstract types, not concrete implementation classes. If a function parameter or return type is a concrete `*Impl` class, that is a violation.
Remediation: extract an interface and expose that instead.

**CORE6 — No platform-specific code in shared modules** (P0)
Files in `commonMain` source sets must not use platform-specific APIs (Android SDK, JVM-only classes, etc.) directly. Use `expect`/`actual` declarations for platform differences.
Remediation: move platform code to `androidMain` / `jvmMain` / `wasmMain` with an `actual` implementation and an `expect` declaration in `commonMain`.

### Module boundary rules

**CORE7 — No reverse dependencies** (P0)
Framework modules (`framework/`) must not depend on application modules (`edifikana/`, `runasimi/`, `flyerboard/`).
Remediation: extract shared abstractions into a lower-level module or pass them via interfaces.

**CORE8 — Architecture module purity** (P1)
`architecture/` modules must not contain business logic. They should only define base classes, interfaces, and contracts.
Remediation: move business logic to the appropriate application module.

### Testing rules

**CORE9 — Public API has tests** (P1)
Every public class or function in a framework module must have at least one test covering its primary behaviour.
Remediation: add a test in the `jvmTest` (or `test`) source set using MockK and `CoroutineTest` as the base class.

**CORE10 — No magic values in tests** (P2)
Test data must use named constants or clearly named variables, not bare string/integer literals, so the test communicates intent.
Remediation: extract the literal into a named `val` at the top of the test class or companion object.

### Build Tools

Create a subagent to run the `release` task for the modified components. This will trigger the compiler, linter and static analyzer to run and generate a report.
Gather the final result and the list of errors reported.

## Step 3 — Produce output

Read `.claude/skills/_shared/output-format.md` and follow those instructions.
