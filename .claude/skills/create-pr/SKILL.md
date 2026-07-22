---
name: create-pr
description: "Draft and create a GitHub PR for the current branch. Reads git history and diff to fill in the PR template (Summary, Demo, References, Test plan) then opens the PR via gh CLI. Use whenever asked to create or open a pull request."
allowed-tools: Read, Bash, Glob, Grep
---

# create-pr — Draft and Create a Pull Request

## Purpose

Produce a fully populated PR description from the current branch's commits and diff, then create the PR via `gh pr create`. The output must follow `.github/pull_request_template.md` exactly.

---

## Step 1 — Gather branch context

Run all of the following in parallel:

```bash
# Commits ahead of main
git log main...HEAD --oneline

# File-level diff summary
git diff main...HEAD --stat

# Full list of changed files
git diff main...HEAD --name-only --diff-filter=d

# Full diff (for reading what actually changed)
git diff main...HEAD
```

From the commit messages, extract:
- **Module tag(s)** — the `[TAG]` prefix on each commit (e.g. `[EDIFIKANA]`, `[FRAMEWORK]`, `[DEVTOOLS]`). If multiple tags appear, use the dominant one for the PR title.
- **Issue numbers** — any `#NNN` references in commit messages (these become References).
- **`[ignore-commit]` commits** — exclude these from the PR summary entirely.

---

## Step 2 — Read the PR template

```
Read: .github/pull_request_template.md
```

This is the structure you must fill in. Do not invent sections that are not in the template.

---

## Step 3 — Look for related artifacts

### Issue plan documents
If any issue numbers were found in Step 1, check for a matching plan file:
```
Glob: ~/.claude/plans/issue-<NNN>-*.md
```
Read any that exist — they provide background, scope, and acceptance criteria that belong in the Summary and Test plan.

### Mock files
If the diff touches any file under `edifikana/mockups/` or `*/mockups/`, note the mock filenames — they are relevant References.

### Screenshots
If the diff adds or modifies files matching `**/screenshots/**` or `**/*.png` or `**/*.jpg`, list them — they belong in the Demo section.

---

## Step 4 — Draft the PR title and body

### Title
Format: `[MODULE] Short description`

- Use the dominant module tag from Step 1.
- The description should be a concise present-tense summary of the overall change (not a list of every commit).
- 70 characters max.

### Body — fill in each template section:

**Summary**
- One bullet per logical change group (not one per commit).
- Lead with the "what" and include the "why" where it is non-obvious.
- If a feature is intentionally incomplete (placeholder, coming-soon stub), say so explicitly.

**Demo**
- If screenshot files were found in Step 3, list their paths as relative links.
- If no screenshots exist but the change is visual (any `Screen.kt`, `Preview.kt`, or composable file was changed), add a placeholder: `_Add screenshots or screen recording before merging._`
- If the change is non-visual (tests, build config, framework code only), write `N/A — no UI changes.`

**References**
- List every issue number found in Step 1 as `- Closes #NNN` or `- Related: #NNN` (use `Closes` only when this PR fully resolves the issue).
- If the plan file read in Step 3 has a `**This Slice's GitHub Issue:**` / `**Parent GitHub Issue:**` header (a plan-issue split slice), this PR closes the *slice's own* issue, not the parent — use `- Closes #<slice-issue>` plus `- Part of #<parent-issue>` (don't `Closes` the parent; the other slices are what actually close it in full).
- If mock files were identified in Step 3, link them: `- Mock: edifikana/mockups/<file>.html`
- If plan files were read in Step 3, link them: `- Plan: ~/.claude/plans/<file>.md`
- If nothing applies, write `N/A`.

**Test plan**
- One checkbox per distinct verification step.
- Cover: happy path, edge cases touched by the diff, and any new or changed tests.
- If new test files were added, include a checkbox to run them with the exact Gradle command.
- Be specific — "Navigate to X and confirm Y" is better than "Test the feature".

---

## Step 5 — Present the draft for approval

Show the complete draft title and body to the user and ask:

> "Does this look good, or would you like to change anything before I create the PR?"

Do **not** call `gh pr create` until the user explicitly approves. If the user requests changes, update the draft and confirm again.

---

## Step 6 — Create the PR

Once approved, run:

```bash
gh pr create --title "<title>" --body "$(cat <<'EOF'
<body>
EOF
)"
```

Return the PR URL to the user. This is the last step of the local pipeline — from here, CI
runs automatically and the PR needs a human review/approval before merge. If CI fails or a
reviewer requests changes, address them and push updates to the same branch; there's no need to
re-run `create-pr` itself unless the PR wasn't opened yet.
