---
name: plan-issue
description: Plan work for any Edifikana GitHub issue by fetching the issue via MCP, reading the wiki and monorepo, and producing a structured wiki plan document — splitting into a dependency-ordered chain of PR-sized plans when the issue spans multiple layers, each filed as its own linked GitHub issue, plus separate linked issues for any deferred follow-up work. Use when starting work on a new issue or asked to plan any GitHub issue.
allowed-tools: Read, Write, Glob, Grep, Bash, WebFetch, mcp__github__issue_read, mcp__github__list_issues, mcp__github__search_issues
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
   Glob: edifikana/models/src/**/*Models.kt
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
6. **Follow-up work** — anything this issue's research surfaces that should explicitly **not** be done as part of it (a related gap found by accident, a descoped piece of the original ask, a "do this too" temptation worth resisting). Each one becomes its own linked GitHub issue later in this skill — capture it now with a one-line title and a sentence on why it's deferred, don't just mention it in prose and lose track of it.

---

## Step 7: Decide PR Sizing

Not every issue should become a single PR. Decide now, before writing the plan, whether this
issue must be split into a dependency-ordered chain of smaller plans — one plan document per
PR-sized slice.

### When to keep it as one PR

- The issue touches only one layer (pure DB, pure shared-model, pure backend, or pure
  frontend), OR
- It touches two adjacent layers but the smaller one is trivial (e.g. a one-field model change
  alongside a single backend method).

### When to split

Split if **any** of these hold:

- The issue spans backend AND frontend as full units of work (a new entity end-to-end) —
  backend Kotlin and frontend Compose are rarely reviewed in the same pass, and a combined diff
  is harder to review and to revert independently.
- The Technical Scope identified in Step 6 would touch **3 or more** of: DB / shared models /
  API contracts / backend / frontend.
- The reference implementation found in Step 4 suggests the finished diff will exceed roughly
  15 files or 500 lines across more than one layer family (DB+backend count as one family;
  frontend is its own family).
- Any slice can be built, tested, and merged on its own without the rest existing yet — a strong
  signal it deserves its own PR and its own review pass.

### How to split

Follow the natural layer seams, in dependency order (earlier slices must merge to `main` before
later ones can start):

1. **Foundation** — DB migration + shared models + API contract. Usually small; unblocks the
   next two slices.
2. **Backend** — Datastore → Service → Controller + tests, built against the Foundation slice.
3. **Frontend** — Screens/ViewModels/UIState/Event + tests, built against the Backend slice
   (needs it merged first — the frontend calls the real API, not a stub).

Combine adjacent slices if one is trivially small — e.g. fold Foundation into Backend if there's
no new table. Never fold Frontend into anything else; when it exists it always gets its own PR.

If the issue does **not** split, skip to Step 8 and produce exactly one plan document.

If it **does** split, produce one plan document per slice in Step 8, and record the chain
explicitly in each slice's Dependencies section: `Depends on: issue-NNN-partK-<slug>.md (must
merge first)`.

---

## Step 8: Write the Plan Document

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
- [ ] <for any new or changed Screen/Preview: "`<Screen>Preview` matches `<mock-name>.html` for
      every modeled use-case" — this is checked via the mock fidelity check, not a unit test>
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

## Follow-up Issues

- <Title> — <1–2 sentences: what's deferred and why it's out of scope for this issue>
  **Filed as:** TBD — filed after plan approval, see Step 10/Step 12
...
(or "None identified" if the research turned up no deferred work)

## Implementation Notes

- Follow the pattern from `<ReferenceController.kt>` / `<ReferenceService.kt>` / `<ReferenceDatastore.kt>`
- <Specific guidance on tricky operations, atomicity, RBAC wiring, Koin registration, etc.>
- <Quote exact Supabase Kotlin syntax for any non-standard operations>
```

**If Step 7 decided to split the issue**, produce one of these documents per slice, each scoped
to only that slice's layers (omit Technical Scope subsections, Test Coverage entries, and
Acceptance Criteria that belong to a different slice). Each slice gets its own GitHub issue
(filed in Step 12), so the header changes shape:

```markdown
# Issue #NNN — <Title> (Part K: <slice name>)

**Phase:** <Phase from issue labels or milestone>
**Epic:** <Epic category>
**Parent GitHub Issue:** #NNN
**This Slice's GitHub Issue:** TBD — filed after plan approval, see Step 10/Step 12
**Slice K of N** — <dependency note, e.g. "depends on Part 1 merging first">
```

In each slice's Dependencies section, add the sibling-plan chain entry in addition to any
GitHub issue dependencies:

```
## Dependencies

- issue-NNN-part1-<slug>.md — must merge first (provides <what this slice needs>)
- #NNN — <what that issue provides that this one needs>
```

The first slice in the chain has no sibling-plan dependency, only GitHub issue dependencies (if
any).

---

## Step 9: Save the Plan(s) to Local Claude Plans

**Single PR (Step 7 did not split):** save the completed plan to:

```
~/.claude/plans/issue-NNN-<short-slug>.md
```

Where `<short-slug>` is a 2–5 word kebab-case summary of the issue title.

Examples:
- `issue-424-common-area-api-backend.md`
- `issue-405-occupant-management.md`
- `issue-394-rls-policies.md`

**Split into multiple PRs:** save one file per slice, numbered in dependency order:

```
~/.claude/plans/issue-NNN-part1-<slug>.md
~/.claude/plans/issue-NNN-part2-<slug>.md
~/.claude/plans/issue-NNN-part3-<slug>.md
```

Examples:
- `issue-424-part1-common-area-db.md`
- `issue-424-part2-common-area-api-backend.md`
- `issue-424-part3-common-area-ui.md`

Filenames stay fixed (`partK` scheme) even once a real child GitHub issue is filed for that
slice in Step 12 — the real issue number is recorded inside the file's header, not in the
filename.

---

## Step 10: Draft Child & Follow-up GitHub Issues

Do not file anything yet — this step only **drafts** the issues so they can be reviewed
alongside the plan in Step 11. Filing happens in Step 12, and only after explicit approval:
creating GitHub issues is a visible, external action (same category as opening a PR), never
something this skill does silently.

### If the issue was split (Step 7)

Draft one child issue per slice:

- **Title:** `[<slice name>] <short description> (Part K of N — #NNN)`, e.g.
  `[Backend] Org-wide property list + RLS fix (Part 1 of 2 — #356)`
- **Body:** a condensed version of the slice's plan document — Goal, Technical Scope summary,
  Acceptance Criteria, Dependencies (including the sibling-slice merge order) — plus a link line:
  `Part of #NNN. See also #<sibling-issue> for the other part(s) of this work.` (sibling issue
  numbers are filled in once known — if slices are filed in the same batch, note "the other
  part(s), filed alongside this issue" instead of guessing a number).
- **Labels/milestone:** match the parent issue's labels and milestone unless the plan's Phase
  clearly indicates otherwise.

### If any Follow-up Issues were identified (Step 6 / Step 8's Follow-up Issues section)

Draft one issue per follow-up:

- **Title:** a short, standalone description of the deferred work — it should make sense to
  someone who has never seen the parent issue.
- **Body:** the 1–2 sentence description from the plan's Follow-up Issues section, expanded
  with enough context to act on independently, plus: `Follow-up from #NNN — <why it was
  deferred>.`
- **Labels/milestone:** match the parent issue's labels; leave the milestone unset unless the
  plan gives a clear reason to set one (follow-ups are typically unscheduled).

If Step 7 did not split and Step 6 found no follow-ups, skip straight to Step 11 with nothing
to draft.

---

## Step 11: Present a Summary to the User

After saving the plan(s) and drafting any child/follow-up issues, present:

1. **What the issue is about** — one sentence
2. **Sizing decision** — single PR, or split into N slices, and why (cite which Step 7 trigger applied)
3. **Layers in scope per slice** — which of: DB / shared models / API / backend / frontend
4. **Reference implementation used** — the existing code pattern this issue should follow
5. **Dependencies** — what must be done first, including slice-to-slice merge order if split
6. **Open questions** — anything that needs a decision before implementation starts
7. **Plan location(s)** — the path(s) where the plan file(s) were saved
8. **Drafted issues** — the full title + body of every child/follow-up issue drafted in Step 10,
   for the user to review, edit, or reject individually — not just a list of titles

Ask the user to review the plan (or each slice) and the drafted issues, and confirm or correct
anything before proceeding. Do **not** start writing implementation code, and do **not** file
any GitHub issues, until the user explicitly approves both the plan and the drafts.

---

## Step 12: File the Approved Issues

Only after explicit approval in Step 11. Prefer an MCP GitHub write tool if one is available in
this session; otherwise use the `gh` CLI (matching `create-pr`'s established convention of using
`gh` for writes even though reads in this skill go through MCP):

```bash
gh issue create --repo codeHavenX/MonoRepo --title "<title>" --body "$(cat <<'EOF'
<body>
EOF
)"
```

For each created issue:

1. Record the returned issue number back into that slice's plan file header (replacing the
   `TBD` placeholder for `**This Slice's GitHub Issue:**` or the follow-up's `**Filed as:**`
   line) — this is what `implement-plan` and `create-pr` will read later, so it must be accurate
   before implementation starts.
2. If this is a split issue with more than one slice, once all sibling issue numbers are known,
   go back and fill in the `#<sibling-issue>` reference left as a placeholder in each other
   slice's body (via `gh issue edit <N> --body "..."` or an MCP equivalent) so every child issue
   cross-links every sibling, not just the parent.

Once every drafted issue is filed, post a single comment on the **parent** issue (#NNN) listing
all of them, so the original issue has the reverse link too:

```bash
gh issue comment NNN --repo codeHavenX/MonoRepo --body "$(cat <<'EOF'
Split into the following issues:
- #<child1> — <slice/follow-up title>
- #<child2> — <slice/follow-up title>
...
EOF
)"
```

Present the final list of filed issue numbers (and their URLs) to the user. Implementation
continues via the `implement-plan` skill — for a split issue, slices must be implemented,
reviewed, and merged strictly in the recorded dependency order, one PR at a time, and each
slice's branch/commits should reference **that slice's own filed issue number**, not the parent
issue's.
