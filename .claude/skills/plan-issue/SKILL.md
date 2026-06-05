---
name: plan-issue
description: Plan work for any Edifikana GitHub issue by fetching the issue via MCP, reading the wiki and monorepo, and producing a structured wiki plan document. Use when starting work on a new issue or asked to plan any GitHub issue.
allowed-tools: Read, Write, Glob, Grep, WebFetch, mcp__github__issue_read, mcp__github__list_issues, mcp__github__search_issues
---

# Plan Issue — Edifikana Issue Planning Skill

## Purpose

Produce a structured, consistent plan document for an Edifikana GitHub issue every time. The same research steps always run in the same order so nothing gets missed.

## Required Information

Ask the user for the **GitHub issue number** if it was not provided with the skill invocation.

---

## Step 1: Fetch the GitHub Issue

Use the MCP tool — never `gh` CLI:

```
mcp__github__issue_read
  owner: "codeHavenX"
  repo:  "MonoRepo"
  issue_number: <N>
```

Record:
- Title, full body, labels, milestone, assignees
- Any referenced issue numbers (`#NNN`) mentioned in the body — these are likely dependencies or related work
- Any linked PRs

---

## Step 2: Read the Architecture Instructions

Always read this file first — it is the source of truth for structure and conventions:

```
Read: <project-root>/.ai/instructions.md
```

Key things to note:
- Back-end layer order: **Controllers → Services → Datastores**
- Front-end files per feature: Screen, ViewModel, UIState, Event, Preview + ViewModelTest
- Commit format: `[MODULE] Short description (#issue-number)`
- Branch naming: `<user>/<issue-number>-<short-description>`

---

## Step 3: Read the Wiki

Always read these wiki files. They live at `<project-root>/../wiki/`.

### Always Read
- `Projects/Architecture/back-end.md`
- `Projects/Architecture/front-end.md`
- `Projects/Architecture/overview.md`
- `Projects/Edifikana/Design/application-design.md`
- `Projects/Edifikana/Development/mvp-project-plan.me`
- `Projects/Edifikana/Development/tasks/phase-*.md`
- `Projects/Edifikana/Development/tasks/issues/` — list all files, then read the 2–3 most relevant to the current issue (same feature area, same phase, or referenced in the issue body). Some of these may be out of date, so use them for reference, but not treated as hard requirement.

### Read if Roles/Permissions are involved
- `Projects/Edifikana/Architecture/roles-permissions.html` (first 150 lines)

### Read if DB schema is involved
- `Projects/Edifikana/Architecture/entity-relationship.md` (first 150 lines — the file is large)

---

## Step 4: Explore the MonoRepo for Patterns

Identify which layers are touched by this issue (backend, frontend, shared models, DB), then search only the relevant layers. Use the most closely related existing implementation as the reference pattern.

### Always check for existing boilerplate or TODOs
```
Grep: "TODO" in the feature-relevant path
```

### For Backend Issues (Controller / Service / Datastore)

1. Find the closest existing Controller as the reference implementation:
   ```
   Grep: "class \w+Controller" path: edifikana/back-end/
   ```
2. Read that Controller, its companion Service, and its Datastore (both interface and Supabase implementation).
3. Note: the Supabase implementation class is always named `Supabase<Entity>Datastore`.
4. Check Koin DI registration in the backend DI module.

### For Frontend Issues (Screen / ViewModel / UIState / Event)

1. Read the Screen, ViewModel, UIState, and Event for that feature.
2. Ensure we have a mock for the requested screen. We cannot do an implementation plan for a screen withouth midium or high fidelity mocks.
2. Check the navigation router for how routes are registered.
3. Check the DI module for how the ViewModel is registered with Koin.
4. Check the existing `/create-*` skills to create an initial component and follow the guidelines to wire it up.

### For Shared Models / API Contracts

1. Find existing domain models:
   ```
   Glob: edifikana/shared/src/**/*Models.kt
   ```
2. Find existing API interfaces:
   ```
   Glob: edifikana/api/src/**/*Api.kt
   ```
3. Note `@JvmInline value class` pattern for typed IDs, `@Serializable` on all network models.

### For Database / Migration Issues

1. Find existing SQL migration files:
   ```
   Glob: edifikana/**/*.sql
   ```
   or
   ```
   Glob: edifikana/**/migration*/**/*
   ```
2. Note table naming conventions, column naming (snake_case), and RLS policy patterns.

---

## Step 5: Check Supabase Kotlin Docs (DB/Datastore changes only)

Skip this step if the issue does not touch the datastore or database layer.

If the issue involves **any** of the following, fetch the relevant reference page:
- New PostgREST queries (select, insert, update, upsert, delete, filtering)
- RLS policies
- Supabase Storage operations
- Supabase Auth operations
- RPC / PostgreSQL functions called from Kotlin

```
WebFetch: https://supabase.com/docs/reference/kotlin/
```

Navigate to the specific section for the operation in question. Quote the exact Kotlin client syntax in the Implementation Notes section of the plan.

---

## Step 6: Identify Layers, Dependencies, and Open Questions

After research, determine:

1. **Which layers are in scope?** (shared models / API contracts / backend / frontend / DB)
2. **Blocking dependencies** — issues that must be done first (look at issue body + the referenced issues you found in Step 1)
3. **Downstream dependents** — issues that will build on this one
4. **Open questions** — anything ambiguous, any decision that was not made in the issue body
5. **Risks and gotchas** — anything unusual found in the codebase (edge cases, existing TODOs, partial implementations, deprecated patterns)

---

## Step 7: Write the Plan Document

Produce a structured plan following this template exactly. Omit any section that does not apply to this issue (e.g., omit "Frontend" for a backend-only issue).

```markdown
# Issue #NNN — <Title>

**Phase:** <Phase from issue labels or milestone, e.g., "Phase 5 — Occupants">
**Epic:** <Epic category, e.g., "API Contracts + Backend Services">
**GitHub Issue:** #NNN

## Goal

<1–2 sentences: what this issue accomplishes and what it unblocks>

## Background & Context

<Why this issue exists, what problem it solves, what depends on it, and how it fits into
the broader system. Reference any related issues or features by number.>

## Technical Scope

### Shared Models (if applicable)

**File: `<Entity>Models.kt`** (shared module)

<List the domain model data classes, enums, and @JvmInline value class IDs.
Show the full Kotlin class definitions.>

### API Layer (if applicable)

**File: `<Entity>Api.kt`**

<Interface with all method signatures.>

**Network Request/Response Models:**

<@Serializable data classes with all fields.>

### Backend (Controller / Service / Datastore) (if applicable)

**`<Entity>Controller.kt`**

<HTTP routes with method + path. Note which auth guard they live under.>

**`<Entity>Service.kt`**

<Key business logic per method: RBAC checks, validation rules, side effects.>

**`<Entity>Datastore.kt` + `Supabase<Entity>Datastore.kt`**

<Supabase PostgREST operations: table name, filter conditions, returning clauses.
Include exact Kotlin Supabase client syntax for non-obvious operations.>

### Database / Supabase (if applicable)

<Migration SQL for new tables or schema changes.
RLS policy statements.
RPC function definitions if needed.>

### Frontend Screens & ViewModels (if applicable)

**Screens:** <list each Screen.kt with a one-line description>

**ViewModels & UI State:**

<UIState data class with all fields and their defaults.
Key ViewModel methods and what they do.>

**Navigation:**

<New Route entries to add. Back-stack behavior after success/cancel.>

## Acceptance Criteria

- [ ] <specific, testable criterion>
- [ ] <include integration test coverage requirements>
...

## Test Coverage

<List the test cases that must be written. Follow the existing test patterns in the monorepo.
Note which existing tests already cover related behavior so we do not duplicate.>

## Dependencies

- #NNN — <what that issue provides that this one needs>
...

## Open Questions / Pending Decisions

- <question or unresolved decision — flag anything that could change the technical approach>
...

## Implementation Notes

- Follow the pattern from `<ReferenceController.kt>` / `<ReferenceService.kt>` / `<ReferenceDatastore.kt>`
- <Specific guidance on tricky operations, atomicity, RBAC wiring, Koin registration, etc.>
- <Quote exact Supabase Kotlin syntax for any non-standard operations>
```

---

## Step 8: Save the Plan to local claude plans

Save the completed plan to:

```
~/.claude/plans/issue-NNN-<short-slug>.md
```

Where `<short-slug>` is a 2–5 word kebab-case summary of the issue title.

Examples:
- `issue-424-common-area-api-backend.md`
- `issue-405-occupant-management.md`
- `issue-394-rls-policies.md`

---

## Step 9: Present a Summary to the User

After saving the plan, present:

1. **What the issue is about** — one sentence
2. **Layers in scope** — which of: DB / shared models / API / backend / frontend
3. **Reference implementation used** — the existing code pattern this issue should follow
4. **Dependencies** — what must be done first
5. **Open questions** — anything that needs a decision before implementation starts
6. **Plan location** — the path where the plan file was saved

Ask the user to review the plan and confirm or correct anything before implementation begins. Do **not** start writing implementation code until the user explicitly approves the plan.
