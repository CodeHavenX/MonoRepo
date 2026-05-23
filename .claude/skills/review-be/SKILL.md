---
name: review-be
description: "Review back-end Kotlin/Ktor code for architecture, naming, layering, and style violations. Use when asked to review controllers, services, or datastore code."
allowed-tools: Read, Glob, Grep, Bash
---

# review-be — Back-End Code Quality Reviewer

## Purpose
Analyze back-end Ktor/Kotlin code in a given scope and produce a prioritized list of findings with remediations.

## Step 1 — Resolve scope

Read `.claude/skills/_shared/scope-resolution.md` and follow those instructions.

Restrict the resolved file list to files under `back-end/` directories. Skip front-end or framework files.

Read each relevant file fully before analysing.

## Step 2 — Apply rules

Read `.claude/skills/_shared/generic-rules.md` and apply those rules to all files.

Then apply the additional back-end-specific rules below.

### Architecture layer rules

**BE1 — Layer annotations and naming** (P0)
Classes must carry the correct annotation and name suffix:

| Annotation | Required suffix | May reference |
|---|---|---|
| `@BackendDatastore` | `Datastore` | — |
| `@BackendService` | `Service` | `@BackendDatastore`, `@BackendService` |
| `@BackendController` | `Controller` | `@BackendService` |

If a class name ends in `Datastore`, `Service`, or `Controller` but lacks the matching annotation, that is a violation (and vice-versa).
Remediation: add the missing annotation or rename the class to match its annotation.

**BE2 — Layer boundary enforcement** (P0)
Allowed call directions:
- `@BackendController` → `@BackendService` only
- `@BackendService` → `@BackendDatastore` or another `@BackendService`
- `@BackendDatastore` → no other back-end layer classes

Any reference that skips a layer or goes upward is a violation.
Remediation: introduce an intermediate layer class or restructure dependencies.

**BE3 — Controller responsibility** (P0)
Controllers must only validate API permissions and extract/parse request parameters. Any business logic (conditionals, calculations, data transformation) found inside a Controller is a violation.
Remediation: move the business logic into a Service method.

**BE4 — Service responsibility** (P1)
Services must contain all business logic. They must not contain direct data access calls to a database or external storage — those belong in Datastores.
Remediation: extract the data access code into a Datastore method and call it from the Service.

**BE5 — Datastore responsibility** (P1)
Datastores must contain optimized data access with minimal business logic. Complex conditionals or calculations unrelated to query optimization are violations.
Remediation: move business logic up to a Service.

### Coroutine dispatcher rules

**BE6 — No direct dispatcher usage** (P1)
Never use `Dispatchers.IO` or `Dispatchers.Main` directly. Inject dispatchers via `@BackgroundDispatcher` or `@UIThreadDispatcher` annotations.
Remediation: replace `Dispatchers.IO` / `Dispatchers.Main` with the injected dispatcher from the constructor or function parameter.

### Testing rules

**BE7 — Service tests exist** (P1)
Every `*Service.kt` must have a corresponding `*ServiceTest.kt` (or `*ServiceImplTest.kt`) in the `test` source set.
Remediation: create the missing test file with at least one test covering the happy path.

**BE8 — Datastore tests exist** (P2)
Every `*Datastore.kt` should have a corresponding `*DatastoreTest.kt` in the `test` source set.
Remediation: create the missing test file.

**BE9 — No mock database in service tests** (P1)
Service tests that interact with data must use a real or in-memory database, not a mocked Datastore, to catch migration mismatches.
Remediation: replace mock with a real test database or integration test setup.

### Build Tools

Create a subagent to run the `release` task for the modified components. This will trigger the compiler, linter and static analyzer to run and generate a report.
Gather the final result and the list of errors reported.


## Step 3 — Produce output

Read `.claude/skills/_shared/output-format.md` and follow those instructions.
