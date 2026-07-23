---
name: verify-plan
description: "Verify an implemented issue plan is actually correct before code-quality review: confirm green build/tests, check every Acceptance Criteria item against real evidence, and flag or exercise anything needing live verification. Use after implement-plan and before running review/review-be/review-ui/review-core, or whenever asked to verify a change is ready for review."
allowed-tools: Read, Write, Glob, Grep, Bash
---

# verify-plan — Correctness Gate Before Code Review

## Purpose

`review`/`review-be`/`review-ui`/`review-core` audit code **quality** (style, architecture,
layering). None of them confirm the change actually **does what the plan says**. This skill is
that gate: green build, tests that really exist and pass, every Acceptance Criteria item backed
by named evidence, and anything that can't be verified by a test called out explicitly instead
of silently assumed. Nothing here should be taken on faith — including claims made by
`implement-plan`'s own handoff notes; re-check them independently.

---

## Required Information

Ask the user for the plan file(s) if not provided:

```
Glob: ~/.claude/plans/issue-<N>-*.md
```

For a split issue (multiple `issue-NNN-partK-*.md` files), verify the slice(s) actually
implemented on the current branch — usually the most recent one. Don't verify slices that
haven't been implemented yet.

---

## Step 1 — Load the plan and the real diff

- Read the plan file(s) in full: Goal, Technical Scope, Acceptance Criteria, Test Coverage,
  Dependencies.
- Get the actual changed files, not the plan's intent:
  ```bash
  git diff main...HEAD --name-only --diff-filter=d
  ```
- Cross-reference: does the diff touch the layers the plan says it should, and nothing beyond
  the current slice's scope? Note any mismatch now — it matters for Step 3.

---

## Step 2 — Green build & tests

Determine touched modules from the diff paths, then for each:

```bash
./gradlew :<module-path>:release --quiet
```

- If the diff touches a Datastore and integration tests exist, run them too:
  ```bash
  ./gradlew :<module>:integTest --quiet
  ```
  If no live Supabase instance is reachable, don't skip silently — record it as **not run,
  requires a live backend** rather than assuming it would pass.
- A **red build or test stops the gate here.** Report the failure and do not proceed to Step 3
  claiming partial success — an unverified acceptance criterion on top of a broken build is
  meaningless.
- Never run `releaseAll` for this — target only the modules actually touched (per
  `.ai/instructions.md`, `releaseAll` is expensive and reserved for when the user asks for it).

---

## Step 3 — Walk Acceptance Criteria with evidence

For every checkbox in the plan's Acceptance Criteria section, resolve it to exactly one of:

- **PASS** — name the specific test (class + method) that exercises it, and confirm that test
  passes in isolation:
  ```bash
  ./gradlew :<module>:test --quiet --tests "com.cramsan.package.SomeTest"
  ```
  A "matches mock" criterion resolves to PASS/fail via the mock fidelity check in Step 4, not a
  Gradle test — run that check before scoring this kind of criterion.
- **NEEDS MANUAL VERIFICATION** — nothing in this skill's toolkit can cover it (a multi-step
  flow needing a real device, a real external service). Do not mark PASS on an assumption.
- **NOT APPLICABLE — THIS SLICE** — the criterion belongs to a downstream slice of a split issue
  that hasn't been implemented yet.

A criterion with no matching test and no manual-verification note is a gap, not a pass — call it
out, don't drop it silently.

---

## Step 4 — Live / visual verification

For anything landing in "NEEDS MANUAL VERIFICATION," or any diff touching `Screen.kt`,
`Preview.kt`, or a `ViewModel.kt`:

- **Regression check** — if the changed composables have
  `@ComponentPreviews`/`@ScreenPreviews`/`@DevicePreviews` annotations, render them with
  Roborazzi (see `.ai/instructions.md`'s Roborazzi section) and diff against the committed
  baseline. After recording, `git status` the module's `screenshots/` directory and
  `git checkout --` anything you didn't intend to touch.
- **Mock fidelity check** — for any acceptance criterion of the form "matches mock" (or any
  screen/preview change at all), read `.claude/skills/_shared/mock-fidelity-check.md` and run it
  independently — do not accept `implement-plan`'s own fidelity pass as evidence; re-render and
  re-compare yourself. This skill only *reports* — resolve every element against the mock's
  actual resolved CSS token values (colors, borders, shadows, corner radii, type scale), not just
  structural order/content, since a shallow pass can miss real styling gaps (see
  `match-mock-fidelity`'s own rationale for a concrete example of this). Report any differences
  found the same way the shared fragment specifies, and resolve the matching Acceptance Criteria
  item to PASS only if this independent run finds none. If differences *are* found, this skill
  does not fix them itself — mark the criterion unmet, note that `match-mock-fidelity` is the
  fix path, and let the user decide whether to run it before re-verifying.
- For behavior a static screenshot can't capture (navigation, a network round-trip, a multi-step
  form), use the `run` skill to launch the app and exercise the golden path plus the edge cases
  named in the plan's Test Coverage / Acceptance Criteria.
- For anything genuinely requiring a human's own eyes or a device/credential this agent doesn't
  have, list it plainly for the user with exact repro steps — never claim it passed.

---

## Step 5 — Test Coverage completeness check

Compare the plan's Test Coverage list against the test files actually added/changed in the diff.
Flag any listed case with no matching test method — this is different from an Acceptance
Criteria gap; it's the plan's own test list not being fully honored.

---

## Step 6 — Produce the Verification Report

```markdown
# Verification Report — Issue #NNN <slug>

## Build & Tests
- `<module>`: `./gradlew :<module>:release --quiet` → PASS | FAIL (paste failure output)
- `<module>` integ tests: PASS | FAIL | NOT RUN (requires live backend)

## Acceptance Criteria
- [x] <criterion> — verified by `<TestClass>.<testMethod>`
- [ ] <criterion> — NEEDS MANUAL VERIFICATION: <what the user needs to check, and how>
- [~] <criterion> — NOT APPLICABLE (belongs to issue-NNN-partK-<slug>.md, not yet implemented)

## Test Coverage Gaps
- <plan test case with no matching test found in the diff>

## Manual Verification Needed
- <item> — repro steps: <...>

## Verdict
READY FOR REVIEW | NOT READY — <reason>
```

**Verdict rule:** never mark READY FOR REVIEW if the build is red, or if any non-NA criterion is
unresolved without an explicit manual-verification note the user has had a chance to act on.

---

## Step 7 — Save the report

```
~/.claude/plans/issue-NNN-<short-slug>-verification.md
```

Using the same slug as the plan file it verifies (append `-partK` for a split slice). This
naming intentionally matches `create-pr`'s existing glob (`issue-<NNN>-*.md`), so the report
gets picked up as a reference automatically — no changes needed there.

---

## Step 8 — Handoff

- **READY FOR REVIEW** — tell the user to proceed to `review` / `review-be` / `review-ui` /
  `review-core`, then `create-pr`.
- **NOT READY** — give a short, ordered punch list of exactly what has to change before
  re-running `verify-plan`. Do not proceed to suggesting review or PR creation.
