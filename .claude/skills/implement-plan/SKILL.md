---
name: implement-plan
description: "Drive an approved issue plan (from plan-issue) to a complete, tested implementation on a feature branch, layer by layer with incremental commits. Use when starting implementation work on an already-planned GitHub issue."
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# implement-plan — Drive a Plan to Implementation

## Purpose

Turn an approved plan document into working, tested code on a feature branch — one logical
commit per completed layer, following the exact patterns and test coverage called out in the
plan. This is the missing link between `plan-issue` (produces the plan) and the `review-*`
skills (audit the finished diff).

## Required Information

Ask the user for the plan file if not provided:

```
Glob: ~/.claude/plans/issue-<N>-*.md
```

If multiple plan files match the issue number (a split issue — see plan-issue's sizing step),
ask which slice to implement, and confirm the dependency order between slices if more than one
remains to be done.

---

## Step 0 — Confirm the plan is approved

This skill never starts from an unread or unapproved plan. If the user hasn't already confirmed
the plan earlier in the conversation, read it back to them in brief (goal, scope, acceptance
criteria) and get an explicit go-ahead before touching git. Do not assume approval silently.

---

## Step 1 — Read the plan and re-check reality

- Read the full plan file.
- Re-read `.ai/instructions.md` if conventions may have shifted since planning.
- Re-check the "reference implementation" files named in the plan's Implementation Notes —
  confirm they still exist and still match the pattern described. Plans can go stale in the
  gap between planning and implementation.
- If the plan's Open Questions section is non-empty, resolve each with the user before writing
  code that depends on the answer. It's fine to start on parts of the plan that don't depend on
  the open question.

---

## Step 2 — Create the branch

```bash
git status   # must be clean — if not, stop and ask how to proceed (stash/commit/abort)
git checkout main && git pull
git checkout -b <user>/<issue-number>-<short-description>
```

Match `<short-description>` to the plan's slug. Never branch from a dirty tree, and never
discard uncommitted work to get there — ask first.

**Which `<issue-number>` to use:** for a plan produced by a split (header has `**This Slice's
GitHub Issue:**`), use *that* number, not the parent's — it's the real issue this specific PR
closes. If that field still reads `TBD`, the child issue hasn't been filed yet (plan-issue's
Step 12) — stop and get it filed first rather than branching against a placeholder. For a
single-PR plan, use the one `**GitHub Issue:**` number as before.

---

## Step 3 — Implementation order

Implement strictly in this dependency order, skipping any layer the plan marks "not
applicable." Commit at the end of each completed layer (see Step 4).

1. **Database / Supabase migration** — apply the plan's SQL. Verify table/column naming matches
   existing convention (snake_case), and RLS policies are included if the plan calls for them.
2. **Shared Models** — domain models, typed IDs (`@JvmInline value class`), `@Serializable`
   network models.
3. **API contracts** — interface + request/response models.
4. **Backend, innermost-out**: Datastore → Service → Controller — strict layering, a Controller
   must never reach past its Service. Write the matching test immediately after each class
   (`*ServiceTest`, `*ControllerTest`, integration test for the datastore) rather than deferring
   all tests to the end; don't move to the next class until its own test is green.
5. **Frontend**: if a new feature/activity/component is needed, scaffold it with the devtools
   CLI first (`./scripts/devtools create feature --name ... --parent ...`, or `create component`
   / `create activity` — see the `create-feature` / `create-component` / `create-activity`
   skills) rather than hand-writing the 5-file skeleton. Then fill in
   ViewModel/UIState/Event/Screen per the plan, wire DI + navigation, and write the
   `ViewModelTest`.

   **Once the Screen and Preview compile, run the `match-mock-fidelity` skill** against this
   screen — it resolves the mock's actual CSS token values, compares them against the Compose
   source (not just the rendered pixels), and iterates fix → re-render → re-compare until clean,
   *before* committing this layer. Don't commit a screen that's known to diverge from its mock,
   and don't settle for a shallow structural check (element order/text content matching is not
   the same as matching the mock's actual colors, borders, shadows, corner radii, and type
   scale — see that skill's own rationale for why this distinction matters).

After writing code + its test for a layer, run:

```bash
./gradlew :<module-path>:release --quiet
```

Fix failures before moving to the next layer. Do not accumulate red layers — a later layer
should never be built on top of a broken one.

---

## Step 4 — Commit discipline

One commit per completed, green layer (small adjacent layers, e.g. shared models + API
contract, may be grouped into one commit if trivially small). Never squash everything into a
single end-of-task commit — reviewers and `create-pr` both rely on commit-level granularity to
judge PR size and reviewability. Use the format from `.ai/instructions.md`:

```
[MODULE] <short description> (#NNN)
```

`#NNN` is this slice's own issue number (see Step 2), not the parent issue's, for a split plan.

Never push. Never amend a commit from this run once a later layer has been built on top of it.

---

## Step 5 — Acceptance criteria pass

Walk every checkbox in the plan's Acceptance Criteria section:

- Mark it done only if there's a concrete test or code path that satisfies it — name the test.
- If a criterion can't be satisfied by this slice alone (e.g. it depends on a downstream PR in a
  split issue), say so explicitly rather than checking it off.

Surface anything unmet to the user now — do not silently drop a criterion on the floor before
handoff.

---

## Step 6 — Handoff

Summarize for the user:

- Branch name and commit list: `git log main..HEAD --oneline`
- Files touched, grouped by layer
- Tests added, and their current pass/fail state
- Acceptance criteria: done / not done / not-applicable-to-this-slice
- Open items that still need a human decision

Point them at the next stage: run **`verify-plan`** to confirm the build/tests are actually
green and every acceptance criterion has real evidence, before `review` / `review-be` /
`review-ui` / `review-core`, then `create-pr`. Do not run `verify-plan`, `review-*`, or
`create-pr` yourself from this skill.
