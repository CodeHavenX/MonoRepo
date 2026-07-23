---
name: match-mock-fidelity
description: "Iteratively fix a Compose screen until it matches its HTML mock: resolve the mock's actual CSS token values, compare against the Compose source (not just the rendered pixels), reuse existing app theme tokens/patterns before inventing new ones, apply fixes, then re-render and re-compare until clean. Use when a screen has already been built and its style doesn't match the mocks, when asked to 'match the mock' or 'fix mock fidelity', or as the fix step within implement-plan's frontend layer."
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# Match Mock Fidelity — Iterative Screen-to-Mock Alignment Skill

## Purpose

Drive an already-implemented Compose screen to zero visual-fidelity differences against its
HTML mock, by repeatedly: resolving the mock's real CSS token values, comparing them against
what the Compose source actually does (not just what the rendered PNG looks like), fixing by
reusing the codebase's existing design tokens and patterns wherever they already exist, and
re-rendering until a full pass finds nothing left to fix.

This is different from `_shared/mock-fidelity-check.md`, which is a **report-only** comparison
used inside `verify-plan` (which must never silently fix things) and as a structural glance
inside `implement-plan`. This skill is the **fix loop** — use it whenever the goal is to actually
close the gap, not just describe it.

### Why this exists

A first-pass structural check ("row order and values match the mock") can pass while the
screen is still visibly wrong. On issue #542 (`OrgDetailScreen`), an initial fidelity pass
declared "no differences" by checking element order and text content — and missed that the
cards had no border/shadow/corner-radius (falling back to bare Material3 defaults), the "Active"
status was plain colored text instead of a pill (no green "success" color token existed
*anywhere* in the app's theme), the org name and info-row values were sized/weighted off the
mock's declared type scale, and a leading icon was missing entirely from one row. Catching this
required reading the mock's CSS custom properties down to their resolved hex/px values and
cross-referencing them against the app's actual theme files (`Shapes.kt`, `Elevation.kt`,
`Color.kt`, `Spacing.kt`) and sibling screens already doing it correctly — not eyeballing a
screenshot.

## When to use

- The user says a screen's style "doesn't match the mocks," or asks to fix mock fidelity.
- As the fix step within `implement-plan`'s frontend layer, once the Screen/Preview compile —
  invoke this skill instead of hand-patching ad hoc.
- Standalone, on any already-built screen (new or old), whenever it needs to be brought into
  alignment with its mock.

## Required Information

Ask for the screen name or `Screen.kt` path if not given. If invoked with no argument mid-
conversation (e.g. right after building or reviewing a screen), infer it from the most recently
touched `*Screen.kt` / `*Preview.kt`.

---

## Step 1 — Locate the mock and the screen

- `Glob: edifikana/mockups/<screen-name>.html`. If no mock exists, stop and say so explicitly —
  there is nothing to compare against.
- Locate `<Feature>Screen.kt`, `<Feature>Preview.kt`, and `<Feature>UIState.kt` under
  `edifikana/front-end/app/src/commonMain/.../features/<section>/<feature>/`.

## Step 2 — Resolve the mock's actual token values

Read the mock's HTML plus `edifikana/mockups/styles/common.css`. For every CSS class the mock's
relevant section uses (`.card`, `.badge-*`, `.info-row`, `.list-item`, `.list-group`, etc.),
resolve every `var(--...)` reference to its concrete literal value — grep `common.css` for
`--token-name:` and note the hex color / px size / font-weight. Build a short table:
`element → property → resolved value` (e.g. `.card → border-radius → 25px`,
`.badge-success → background → #DCFCE7`, `.info-row__value → font-size/weight → 16px / 500`).

This table is the ground truth for Step 4. Compare against these resolved values, never against
the mock's class names alone — a class name matching doesn't mean the token values do.

## Step 3 — Render the current implementation

Follow `.ai/instructions.md`'s Roborazzi section to render every
`@ComponentPreviews`/`@ScreenPreviews`/`@DevicePreviews` preview on the screen:

```bash
./gradlew :<module>:recordRoborazziAndroidHostTest --tests "*<PreviewFunctionName>*"
```

(Use `recordRoborazziDebug` instead on modules still on the legacy Android Gradle plugin — check
`.ai/instructions.md` for which task name applies to this module.) View the resulting PNG(s)
with Read.

## Step 4 — Token-level comparison

For every visual element the mock's use-case shows, compare the Step 2 resolved value against
what the render shows — and cross-reference the **Compose source**, not just the pixels, so you
know exactly what's driving it:

- **Cards** — does `Card(...)` set an explicit `shape`, `border`, `colors` (container color), and
  `elevation`, or is it relying on Material3 defaults? Defaults essentially never match a custom
  mock (wrong corner radius, no border). Watch for this specific gotcha: a non-zero `elevation`
  triggers Material3's tonal-elevation overlay, which tints the container color *even when an
  explicit `containerColor` is set* — a flat gray-blue card instead of the mock's flat white is
  the classic symptom, and the fix is an explicit `containerColor` (often
  `colorScheme.surfaceContainerLowest` for a mock that wants pure white), not removing the
  elevation.
- **Badges / status pills** — is it a shaped `Surface`/container with background + content
  color, or plain `Text` with just a color and no background? Does the color used exist as a
  real semantic token, or is an unrelated one standing in for it (e.g. `colorScheme.primary`
  filling in for a "success" green that has no token)?
- **Typography** — does the `Text`'s `style` plus any `fontWeight` override match the mock's
  resolved font-size/weight, or is it an unexamined Material3 type-scale default? Never assume
  `headlineSmall`/`bodyMedium`/etc. matches a custom mock scale — always check the actual
  rendered size/weight against Step 2's table.
- **Icons / structural elements** — is every element the mock's use-case shows (icon-in-circle
  leading elements, dividers, chevrons, empty-state art) actually present, not just the text
  content? A missing element is a finding, not a style nuance.

List every mismatch found as: `element — mock value → current code (file:line) → what's wrong`.

## Step 5 — Fix by reusing existing patterns first

For each mismatch, **before writing a new value**, search the codebase for how this same visual
pattern is already solved elsewhere — reuse beats invention:

1. Grep sibling screens in the same feature area (and `ui-components`) for the same kind of
   element (another bordered card, another status pill, another icon-in-circle row). A match
   usually already exists — established tokens like `Shapes.pill`, `Elevation.resting`,
   `Spacing.*`, the `IconBadge` component, or a sibling screen's exact `Card(...)` parameters.
2. Check the theme package (`ui-components/.../theme/`: `Color.kt`, `Shapes.kt`, `Elevation.kt`,
   `Spacing.kt`, and any semantic-color files already present) for a token whose resolved value
   already matches what the mock wants.
3. Only if genuinely nothing exists (e.g. the mock uses a color family — success, warning, info
   — with no Kotlin equivalent anywhere), add a new token to the theme package, following the
   existing token files' conventions (a small `object` per concern, mirroring `Shapes.kt` /
   `Elevation.kt`'s style). Provide light **and** dark variants if the app supports dark theme,
   even though the mock is light-only — pick values consistent with the existing palette's
   derivation and say explicitly that the dark-mode values are a best-effort extrapolation,
   since the mock doesn't define one. Never hardcode a one-off `Color(0x...)` directly inside a
   `Screen.kt` — a new token belongs in the shared theme package so other screens can reuse it.
4. For typography that needs to match the mock's declared scale but has no corresponding named
   Material3 style, use a local `Text` style override (`.copy(fontSize = ..., fontWeight =
   ...)`) scoped to that composable — don't change the app's global `Typography` for one
   screen's fix unless asked to.

## Step 6 — Rebuild, re-render, re-diff

```bash
./gradlew :<module>:release --quiet
./gradlew :<module>:recordRoborazziAndroidHostTest --tests "*<PreviewFunctionName>*"
git status --porcelain <module-dir>/screenshots/
```

Confirm the build is green. Confirm the screenshot diff is scoped to the screen being fixed —
`git checkout --` anything unrelated swept up by a stale `finalizeTestRoborazziDebug` write (see
`.ai/instructions.md`'s warning on this).

## Step 7 — Loop until clean

Re-run Step 4's token-level comparison against the freshly rendered PNG. If any mismatch
remains, go back to Step 5. Re-check *every* element on each pass, including ones already fixed
— one fix (e.g. an explicit container color) can interact with another (e.g. elevation tint).
Stop only when a full pass finds nothing left to fix.

## Step 8 — Report

Summarize:

- Every fix made, as `element — mock token/value → code change (file:line)`.
- Any new theme tokens added, and why no existing one covered it.
- Anything found to be systemically wrong elsewhere in the codebase (e.g. a sibling screen using
  the same broken card/badge pattern) that was **not** fixed because it's outside the current
  screen's scope — name it explicitly so it isn't silently left for someone else to rediscover.
- Confirmation that the build is green and the screenshot diff is scoped to the intended screen.

This skill does not commit on its own — leave that to the calling workflow (`implement-plan`'s
per-layer commit discipline, or a direct commit if invoked standalone and the user asks for one).
