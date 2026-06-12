---
name: review
description: "Run a full code quality review across any scope: auto-detects front-end, back-end, and framework code and applies all relevant rule sets. Use when asked to review code quality, check for violations, or audit a module/feature/PR."
allowed-tools: Read, Glob, Grep, Bash
---

# review — Full Code Quality Orchestrator

## Purpose
Run all applicable code quality rule sets against a given scope and produce one unified, prioritized findings report.

This skill combines the rules from `review-ui`, `review-be`, and `review-core`. For a targeted review of only one code type, use the focused skill directly.

## Step 1 — Resolve scope

Read `.claude/skills/_shared/scope-resolution.md` and follow those instructions.

## Step 2 — Categorise files

For each file in scope, assign one or more categories:

| Category | Heuristics |
|---|---|
| **UI** | path contains `front-end/`, `ui-components/`, `front-end/app/`, or `ui-catalog/`; or filename ends with `Screen.kt`, `ViewModel.kt`, `UIState.kt`, `Event.kt`, `Preview.kt` |
| **BE** | path contains `back-end/`; or filename ends with `Controller.kt`, `Service.kt`, `Datastore.kt` |
| **Core** | path contains `framework/`, `architecture/`, `shared/`, or `api/` at the repo root level |
| **Generic** | any Kotlin file not matched above |

A file may belong to multiple categories. Read each relevant file fully before analysing. Group your reading by category to keep context focused.

## Step 3 — Apply rule sets

### Generic rules (ALL files)

Read `.claude/skills/_shared/generic-rules.md` and apply those rules to every file in scope.

### UI rules (UI category files)

Read `.claude/skills/review-ui/SKILL.md` — apply all rules under "Feature structure rules" and "Architecture layer rules" to UI files.

### Back-end rules (BE category files)

Read `.claude/skills/review-be/SKILL.md` — apply all rules under "Architecture layer rules", "Coroutine dispatcher rules", and "Testing rules" to BE files.

### Core rules (Core category files)

Read `.claude/skills/review-core/SKILL.md` — apply all rules under "Coroutine and threading rules", "API design rules", "Module boundary rules", and "Testing rules" to Core files. Elevate G1 to P0 for core files.

### Build Tools

Create a subagent to run the `release` task for the modified components. This will trigger the compiler, linter and static analyzer to run and generate a report. 
Gather the final result and the list of errors reported.

## Step 4 — Produce output

Read `.claude/skills/_shared/output-format.md` and follow those instructions.
