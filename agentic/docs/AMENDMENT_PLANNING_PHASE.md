# Amendment — AI-Driven Planning Phase

> Status: Proposed
> Amends: ARCHITECTURE.md §1 (rename), §1.2, §1.3 · TECH_DESIGN.md §4.1, §5.1, §6, §8

---

## Summary

Remove the human-authored **Task List** from the required input documents. Replace it with a
three-stage AI-driven planning process that produces the task list automatically from the other
three documents. Each stage produces a document that the human must review and approve before
the next stage begins. The orchestrator only starts once the final task list has been approved.

---

## Motivation

Requiring humans to author a detailed task list is a high-friction step. The goals, architecture,
and standards documents already contain enough information for an AI to decompose the work.
Generating the task list through a structured, human-gated planning pipeline produces a more
consistent and thorough result, while keeping the human firmly in control at every stage.

---

## Rename: "Validation" → "Planning"

What the architecture and CLI previously called the **validation** phase (Layer 1, the
`agentic validate` command) is renamed to the **planning** phase throughout. The rationale
is that document validation is only the first step of a broader process that also produces the
high-level plan, low-level plan, and task list. Calling the whole thing "validation" understates
its scope.

Concrete renames:
- **Layer 1** in ARCHITECTURE.md: "Input Layer" description changes to reflect that this layer
  owns the full planning pipeline, not just validation.
- **CLI command:** `agentic validate` is replaced by `agentic plan validate` (see §Changes to
  the CLI below). The old command is removed.
- **Terminology:** all references to "the validation phase" in docs and comments are updated to
  "the planning phase". The internal step of reviewing input documents retains the name
  "document validation" to distinguish it from the planning stages that follow.

---

## Changes to the Input Layer (§1.2 and §1.3)

### 1. Required Input Documents

The `TASK_LIST` document type is removed from the required input set. Humans now provide three
documents:

| Document | Purpose |
|----------|---------|
| **Goals & Scope** | What the project is trying to achieve |
| **Architecture / Design** | Technical decisions agents must respect |
| **Standards** | Coding conventions, patterns, constraints |

The task list is no longer human-authored. It is produced at the end of the planning phase
(see §3 below) and becomes a fourth document in the `.agentic/docs/` directory. Its presence
signals that planning is complete and the orchestrator may start.

### 2. Document Validation

Document validation is unchanged in mechanism. It is now the first step within the planning
phase rather than a phase of its own. `agentic plan validate` reviews the three human documents
(Goals/Scope, Architecture/Design, Standards) and produces a Validation Report. The system will
not proceed to the planning stages until all three documents are `validated`.

The agent checks the same criteria as before:
- Goals and requirements are clearly stated
- There are no contradictions between documents
- There are no gaps or ambiguities that would prevent planning

### 3. Planning Stages

After all three documents reach `validated` status, the planning stages begin. The full planning
phase — from `agentic plan validate` through task list approval — must complete before the
orchestrator may start. It is a sequential pipeline; each stage produces a document the human
must approve before the next stage starts.

```
Human provides docs → agentic plan validate
        │
        ▼
Document validation pass
  ├── Issues found → human edits → re-validate
  └── All docs validated
        │
        ▼
Stage 1 — High-Level Plan
  Agent reads: Goals/Scope + Architecture/Design + Standards
  Produces:    high-level-plan.md
        │
        ▼
Human reviews high-level-plan.md
  ├── Approve → continue to Stage 2
  └── Request changes → agent revises; human re-reviews
        │
        ▼
Stage 2 — Low-Level Detailed Plan
  Agent reads: all three input docs + approved high-level-plan.md
  Produces:    low-level-plan.md
        │
        ▼
Human reviews low-level-plan.md
  ├── Approve → continue to Stage 3
  └── Request changes → agent revises; human re-reviews
        │
        ▼
Stage 3 — Task List
  Agent reads: all three input docs + both approved plans
  Produces:    task-list.md
        │
        ▼
Human reviews task-list.md
  ├── Approve → orchestrator may start
  └── Request changes → agent revises; human re-reviews
```

#### Stage 1 — High-Level Plan

The agent produces a concise, human-readable overview of the work to be done. It must cover:
- A summary of the approach to achieving the stated goals
- Major components or areas of the codebase that will be touched
- Key technical decisions, constraints, or trade-offs identified from the architecture/design
  and standards documents
- A rough breakdown of work into logical groups (not yet individual tasks)
- Any risks or open questions that should be resolved before detailed planning begins

#### Stage 2 — Low-Level Detailed Plan

The agent expands the approved high-level plan into a fine-grained design. It must cover:
- A detailed breakdown of each logical group into concrete units of work
- For each unit: proposed approach, affected modules/files, and dependencies on other units
- Resolution of any open questions or risks identified in the high-level plan
- Identification of the critical path through the work

#### Stage 3 — Task List

The agent translates the approved low-level plan into a structured, machine-readable task list.
Each task must include:

| Field | Description |
|-------|-------------|
| **ID** | Unique identifier (e.g. `TASK-001`) |
| **Title** | Short, imperative summary |
| **Description** | What must be implemented and why |
| **Dependencies** | IDs of tasks that must be `done` before this task can start |
| **Implementation Plan** | Step-by-step approach the implementing agent should follow |
| **Testing Plan** | Which tests to write and what they must verify |
| **Acceptance Criteria** | Observable, verifiable conditions for the task to be `done` |
| **Sample Code** | Illustrative snippets where helpful (interfaces, data models, key functions) |
| **References** | Links to relevant sections of the input documents |

The task list format is identical to the current `task-list.md` spec so the orchestrator
(Coordination Layer) requires no changes.

### 4. Human Approval Mechanism

Each planning stage produces a document in `.agentic/docs/`. The human signals approval by
running a CLI command (see §6 below). Until approval is given, the system does not proceed.

If the human is not satisfied, they annotate the document with their feedback and run the
revision command. The agent reads the annotated document and produces a revised version. This
cycle repeats until the human approves.

Human edits to any of the three input documents after planning has begun reset the planning
phase entirely (back to document validation). This mirrors the existing behaviour where any
edit resets all documents to `unreviewed`.

---

## Changes to Data Models (§4.1)

### DocumentType

```kotlin
@Serializable
enum class DocumentType {
    GOALS_SCOPE,
    ARCHITECTURE_DESIGN,
    STANDARDS,
    // TASK_LIST removed — generated, not human-authored
    HIGH_LEVEL_PLAN,       // Stage 1 output
    LOW_LEVEL_PLAN,        // Stage 2 output
    TASK_LIST,             // Stage 3 output — generated, not human-authored
}
```

`TASK_LIST` is retained as a `DocumentType` because the orchestrator still reads from it.
The distinction is in how it is produced: it is now always AI-generated, never human-authored.
The document is only present in `.agentic/docs/` after Stage 3 completes and the human approves.

### PlanningStatus (new)

```kotlin
@Serializable
enum class PlanningStatus {
    NOT_STARTED,                  // `agentic plan validate` has not been run yet
    AWAITING_DOCUMENT_VALIDATION, // Validation pass in progress or pending human doc edits
    STAGE_1_IN_PROGRESS,          // Agent generating high-level plan
    STAGE_1_PENDING_APPROVAL,
    STAGE_2_IN_PROGRESS,
    STAGE_2_PENDING_APPROVAL,
    STAGE_3_IN_PROGRESS,
    STAGE_3_PENDING_APPROVAL,
    COMPLETE,                     // Task list approved; orchestrator may start
}
```

`PlanningStatus` is derived from the filesystem (presence of plan documents and approval
marker files), consistent with the "filesystem as source of truth" principle.

---

## Changes to Component Interfaces (§5.1)

### PlanningService (new)

```kotlin
/**
 * Drives the three-stage planning pipeline.
 * Each stage is triggered explicitly by the CLI — the service does not auto-advance.
 */
interface PlanningService {
    /** Returns the current planning status derived from disk. */
    fun status(): PlanningStatus

    /** Runs Stage 1: reads validated input docs, writes high-level-plan.md. */
    suspend fun generateHighLevelPlan(): PlanDocument

    /** Runs Stage 2: reads input docs + high-level plan, writes low-level-plan.md. */
    suspend fun generateLowLevelPlan(): PlanDocument

    /** Runs Stage 3: reads all above, writes task-list.md. */
    suspend fun generateTaskList(): PlanDocument

    /**
     * Revises the current stage's output document based on human annotations.
     * The human writes their feedback directly into the document (e.g. as comments
     * or inline notes) before calling this. The agent reads the annotated document
     * and produces a revised version, replacing the file in place.
     */
    suspend fun revise(stage: PlanningStage): PlanDocument

    /** Records human approval for a planning stage. Writes an approval marker file. */
    fun approve(stage: PlanningStage)
}

enum class PlanningStage { HIGH_LEVEL_PLAN, LOW_LEVEL_PLAN, TASK_LIST }

data class PlanDocument(
    val stage: PlanningStage,
    val path: Path,
)
```

---

## Changes to Persistence (§6)

```
.agentic/
  docs/
    goals-scope.md
    architecture-design.md
    standards.md
    validation-report.md
    high-level-plan.md          # Stage 1 output (absent until generated)
    high-level-plan.approved    # Marker written by `agentic plan approve stage1`
    low-level-plan.md           # Stage 2 output
    low-level-plan.approved     # Marker
    task-list.md                # Stage 3 output
    task-list.approved          # Marker; presence allows orchestrator to start
    reviewers/
      ...
  tasks/
    ...
  worktrees/
    ...
```

**Approval markers** are empty files. Their presence is the only signal the system needs;
their absence means the stage is not yet approved. Deleting a marker resets that stage.

**Planning status derivation:**

| What exists | Derived PlanningStatus |
|-------------|------------------------|
| No validated docs | NOT_STARTED or AWAITING_DOCUMENT_VALIDATION |
| Validated docs, no `high-level-plan.md` | STAGE_1_IN_PROGRESS (or pending `plan generate` trigger) |
| `high-level-plan.md`, no `.approved` | STAGE_1_PENDING_APPROVAL |
| `high-level-plan.approved`, no `low-level-plan.md` | STAGE_2_IN_PROGRESS (or pending trigger) |
| `low-level-plan.md`, no `.approved` | STAGE_2_PENDING_APPROVAL |
| `low-level-plan.approved`, no `task-list.md` | STAGE_3_IN_PROGRESS (or pending trigger) |
| `task-list.md`, no `.approved` | STAGE_3_PENDING_APPROVAL |
| `task-list.approved` | COMPLETE |

---

## Changes to the CLI (§8)

### `plan` subcommand group (replaces `validate`)

The standalone `agentic validate` command is removed. All planning-phase operations are now
under `agentic plan`:

```
agentic plan validate            Run a document validation pass and print findings
agentic plan status              Print current planning phase and next required action
agentic plan generate            Advance to and run the next planning stage
agentic plan approve <stage>     Approve the current stage output (stage: stage1 | stage2 | stage3)
agentic plan revise <stage>      Re-run the current stage after human annotations
```

`agentic plan validate` has the same behaviour as the old `agentic validate` — it reviews the
three input documents, writes the Validation Report, and exits non-zero if blocking issues remain.
After a clean pass it prints a prompt reminding the user to run `agentic plan generate`.

`agentic plan generate` determines which stage to run based on the current `PlanningStatus`
and runs it. The human does not need to specify the stage — the system infers it.

### Updated `start` command

`agentic start` now checks for `task-list.approved` before proceeding. If the marker is
absent, the command exits with an error message directing the user to complete the planning
phase first.

---

## Impact on Existing Components

| Component | Impact |
|-----------|--------|
| `DocumentStore` | `TASK_LIST` doc status is now set by `PlanningService`, not the user |
| `TaskStore` | No change — still reads from `task-list.md` |
| `ValidationService` | No change in logic; rename to `DocumentValidationService` to avoid confusion with the broader planning phase |
| `Orchestrator` | Checks `task-list.approved` marker at startup; no other changes |
| `ReviewerAgent.reviewDocuments` | Now also receives planning documents for advisory review |
| Scaffolding (`agentic init`) | No longer scaffolds a `task-list.md` template |

---

## Non-Goals

- This amendment does not change how tasks are executed once the orchestrator starts.
- The amendment mechanism (§1.4) is unchanged; agents may still propose amendments to any
  input document during execution. If a planning document is amended, the human decides
  whether to re-run the downstream planning stages.
- This amendment does not introduce streaming or interactive output during plan generation.
  Each stage runs to completion and writes its output as a file.
