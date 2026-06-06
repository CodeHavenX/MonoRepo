---
name: refine-mock
description: Create or refine HTML mockups for a single screen. Gathers visual design spec, responsive layout strategy, component patterns, navigation context, and screen use-cases before writing any HTML. Use when asked to create a new screen mockup or refine an existing one.
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# Refine Mock — Screen Mockup Creation & Refinement Skill

## Purpose

Produce or update a high-fidelity HTML mockup for a single screen, grounded in the visual design system, responsive layout strategy, navigation structure, and a clear enumeration of the use-cases the screen must model.

The same research steps always run in the same order — context first, screen specifics second, plan third, execution last.

---

## Step 1 — Identify the Target Screen

Ask the user for the following if not already provided:

1. **Screen name or file** — e.g. `add-property`, `task-detail`, `resident-home`. If a file already exists at `edifikana/mockups/<name>.html`, this is a refinement; otherwise it is a new screen.
2. **New or refinement?** — Confirm which mode applies based on whether the file exists.
3. **Specific goal** (refinement only) — What does this round of refinement need to accomplish? E.g. "add an error state", "fix desktop layout", "add a confirmation modal". If the user has not stated a goal, ask before proceeding.

Do not proceed to Step 2 until all three questions are answered.

---

## Step 2 — Load Foundational Context

Read all four sources in parallel. These are required for every invocation regardless of screen.

### 2a — Visual Design System
```
Read: <project-root>/../wiki/Projects/Edifikana/Design/visual-design-system.md
```
Note: colour tokens (`--color-*`), border-radius tokens (`--radius-*`), typography scale, spacing scale, component specifications (buttons, cards, inputs, badges, FAB, bottom nav).

### 2b — Responsive Layout Strategy
```
Read: <project-root>/../wiki/Projects/Edifikana/Design/responsive-layout.md
```
Note: breakpoints (mobile / tablet / desktop), the required shell structure, the three screen-type classifications (App Shell / Auth / Setup Flow), and the common mistakes table.

### 2c — Common CSS Components and Tokens
```
Read: <project-root>/edifikana/mockups/styles/common.css
```
Note every named CSS class available (`.btn`, `.card`, `.list-item`, `.badge`, `.fab`, `.bottom-nav`, `.tab-bar`, `.modal`, `.bottom-sheet`, `.org-card`, `.task-card`, `.metric-card`, `.avatar`, `.filter-chip`, `.toggle-row`, `.search-bar__input`, etc.) and what each renders. These are the only building blocks to use — do not invent new inline styles for things already covered by a class.

### 2d — Application Design and Navigation Structure
```
Read: <project-root>/../wiki/Projects/Edifikana/Design/application-design.md
```
Note: the overall navigation hierarchy (bottom-nav tabs, settings sub-screens, auth flow, resident vs admin flows), and the screen inventory so the target screen's position in the app can be located.

---

## Step 3 — Scan Existing Mocks for Reference Patterns

```
Glob: <project-root>/edifikana/mockups/*.html
```

From the list of existing files, identify the **1–2 most structurally similar** screens to the target (e.g. another detail screen, another form screen, another list screen in the same nav section). Read those files in full:

```
Read: <project-root>/edifikana/mockups/<similar-screen>.html
```

These become the reference implementation. Note:
- Which shell structure they use (`.app-layout` + `.sidebar-nav` vs auth layout vs setup flow)
- Which active nav item they highlight
- How they handle content padding, section headers, cards, and empty states
- Any JS patterns they use (modals, tab switching, FAB menus)

---

## Step 4 — Read the Existing Screen (Refinement Only)

If this is a **refinement**, read the current file in full:

```
Read: <project-root>/edifikana/mockups/<screen-name>.html
```

Identify what is already present and what the stated refinement goal requires changing.

Skip this step entirely for new screens.

---

## Step 5 — Establish Screen-Specific Understanding

Using the application-design.md content from Step 2d, the existing mocks inventory from Step 3, and the screen name/goal, determine the following. Do not ask the user for these — derive them from the research.

### 5a — Screen Purpose
State the single function this screen serves in one sentence. A well-scoped screen does exactly one thing (e.g. "display the details of a single task and allow the user to mark it complete or delete it"). If the screen appears to cover multiple unrelated concerns, flag this as a design issue in the plan but proceed with what makes sense.

### 5b — Navigation Placement
Determine:
- Which **bottom-nav tab** owns this screen (Dashboard / Properties / Tasks / More, or none for auth/setup flows)
- What the **back target** is (which screen the back arrow or back button returns to)
- Whether this screen appears in the **admin flow**, **resident flow**, or both
- The correct **shell type** per `responsive-layout.md`: App Shell, Auth, or Setup Flow

### 5c — Use-Cases to Model
Enumerate every distinct state or user scenario the screen must represent in the mockup. For most screens this includes some combination of:

| Category | Examples |
|----------|---------|
| **Loading** | Skeleton or spinner while data fetches |
| **Empty state** | No items yet, prompt to create first |
| **Populated / happy path** | Normal content, the primary use case |
| **Error state** | Failed fetch, validation error, permission denied |
| **Confirmation / destructive action** | Modal or inline confirm before delete/leave |
| **Form states** | Blank, partially filled, field-level errors |
| **Role-dependent content** | Admin vs staff vs resident sees different actions |
| **Selection / active state** | An item is selected, a tab is active |

For a **new screen** include all relevant states as distinct sections or commented-out state variants within the file.

For a **refinement**, include only the states described in the goal from Step 1, plus any states that are currently missing from the file and are obviously required.

---

## Step 6 — Present a Plan and Get Confirmation

Before writing any HTML, present the following plan to the user:

```
Screen: <filename>.html  (new | refinement)
Goal: <goal or "initial mock">

Purpose
  <one sentence describing what this screen does>

Navigation Context
  Shell type  : <App Shell | Auth | Setup Flow>
  Nav section : <bottom-nav tab — Dashboard / Properties / Tasks / More / none>
  Back target : <parent screen filename or "none">
  User flow   : <admin | resident | both>

Use-Cases to Model
  1. <use-case name> — <one sentence>
  2. ...

Reference Screens
  - <filename>.html — <why it was chosen as reference>

Changes (refinement only)
  - <specific structural or content change>
  - ...
```

Ask the user: "Does this plan look right? Confirm to proceed or tell me what to adjust."

**Do not write any HTML until the user confirms the plan.**

---

## Step 7 — Write or Update the HTML

Once the plan is confirmed, produce the HTML.

### Structure Rules

1. **Always use the correct shell** from the responsive layout strategy:
   - **App Shell**: `mobile-viewport > app-layout > sidebar-nav + screen > top-bar + screen-content + bottom-nav`
   - **Auth**: `mobile-viewport > screen > screen-content--no-nav > auth-layout`
   - **Setup Flow**: `mobile-viewport > screen > top-bar + screen-content--no-nav`

2. **Sidebar and bottom-nav active states** must match `navigation placement` from Step 5b. Use exactly the same nav items and hrefs as `settings.html` or `dashboard.html` (copy the block verbatim and change only the `--active` class).

3. **No hardcoded values**. Every colour, radius, spacing, and shadow must use a `var(--...)` token from `common.css`. No inline `#hexcolour`, no inline `Npx` for anything covered by a token.

4. **Use existing CSS classes only**. Build every element from the named classes in `common.css`. Only use `style=""` for structural layout values that have no class equivalent (e.g. `display:flex; gap: var(--space-3)`). Never use `style=""` to apply colours, radii, or shadows that have token equivalents.

5. **Model each use-case**. For the happy path, show realistic placeholder content. For secondary states (empty, error, loading, modals), include them as:
   - Separate visible sections separated by a `<div class="divider--thick">`, each prefixed with an `<!-- === USE CASE: Name === -->` comment, OR
   - Hidden elements with `class="hidden"` and a JS toggle if the state overlays the same content area (e.g. modals, confirmation dialogs)

6. **Link to real screens**. All `href` values must point to existing `.html` files in the mockups directory. Use `#` only for actions with no target screen yet (and add an HTML comment explaining where it should go).

7. **Include a `<script>` block** only if the screen needs interactive JS — modal show/hide, tab panel switching, FAB speed-dial. Copy the pattern exactly from an existing screen that has the same interaction.

### Quality Checklist (self-verify before finishing)

Before reporting done, verify each item:

- [ ] Correct shell type applied (`.app-layout` present for App Shell screens)
- [ ] `.sidebar-nav` present with correct `--active` item (App Shell only)
- [ ] `.bottom-nav` present with correct `--active` item (App Shell only)
- [ ] `screen-content` vs `screen-content--no-nav` chosen correctly
- [ ] No hardcoded hex colours or pixel values for design tokens
- [ ] All use-cases from Step 5c are visible in the file
- [ ] All `href` links point to real files (or `#` with a comment)
- [ ] JS interactions (if any) follow existing patterns exactly

---

## Step 8 — Report to the User

After writing the file, report:

1. **File written**: path to the HTML file
2. **Use-cases covered**: list each one
3. **Shell type applied**: and why
4. **Anything omitted or deferred**: with a reason (e.g. "loading skeleton not included — no spinner component exists in common.css yet")
5. **Suggested follow-up**: any obvious next screen to mock or any design question surfaced during the work
