# Agentic — Architecture Document

> Status: Complete

## Overview

Agentic is an autonomous orchestration system that takes human-authored process documents as input and produces pull requests as output. A pool of agents works concurrently, each fully autonomous from task pickup to PR submission. Humans interact with the system exclusively through PR reviews.

```
[Input Docs] → [Validation Loop] → [Orchestrator] → [Agent Pool] → [Pull Requests]
                    ↕ (iterate)                                            ↓
               [Human edits]                                       [Human Review]
                                                                           ↓
                                                                 [Merged or Rejected]
```

---

## Layer 1 — Input Layer

The input layer is the authoritative contract between the human and the system. It owns three responsibilities:

### 1.1 Scaffolding
On demand, the system generates a complete starter document set. This gives new users a concrete starting point and makes the expected input format self-evident. The scaffolded documents include placeholder content illustrating what each section requires.

### 1.2 Semantic Validation

Validation is an iterative loop between the system and the human. It is not a one-shot gate. The orchestrator will not proceed until every document has been individually reviewed and reaches `validated` status.

#### Validation Loop

```
Human provides documents
        │
        ▼
Agent reviews each document independently
        │
        ▼
Findings saved to Validation Report
        │
        ├─ Issues found → status: needs_revision
        │       │
        │       ▼
        │  Human is notified, reviews findings
        │       │
        │       ▼
        │  Human edits documents
        │       │
        │       └── re-run validation on ALL docs ──┐
        │                                           │
        └─ No issues → status: validated            │
                │                                   │
                ▼                              (repeat until
        All documents validated?               all validated)
                │
                ▼
        Proceed to orchestrator
```

#### Per-Document Validation Status

Each document is tracked independently:

| Status | Meaning |
|--------|---------|
| `unreviewed` | Not yet examined by the agent |
| `in_review` | Agent is currently reviewing this document |
| `needs_revision` | Agent found issues; human action required |
| `validated` | Document passed review; no outstanding issues |

The orchestrator only starts when **all** documents are in `validated` status.

Any human edit to any document resets all documents to `unreviewed` and triggers a full re-validation pass. This ensures the document set is always evaluated as a whole — a change in one document may introduce or resolve issues in another.

#### Validation Report

The agent's findings are saved to a persistent **Validation Report** document alongside the input docs. For each issue found, the report records:
- Which document is affected
- A description of the issue (inconsistency, gap, ambiguity, or area for improvement)
- Severity (blocking vs. advisory)
- Current status (open, addressed, dismissed)

The Validation Report is updated on each review pass. Issues can be marked as addressed (after human edits) or dismissed (human chose to accept the risk). Only blocking issues prevent the orchestrator from starting — advisory issues are surfaced but do not block progress.

#### What the agent looks for
- Are the goals and requirements clearly stated?
- Is the task list present, and are tasks sufficiently described to act on?
- Are there contradictions between documents?
- Are there gaps that would prevent an agent from completing a task?
- Are there areas of ambiguity that could lead to inconsistent agent behavior?

### 1.3 Document Structure
The input documents are human-owned and human-maintained. They include:

| Document | Purpose |
|----------|---------|
| **Goals & Scope** | What the project is trying to achieve |
| **Architecture / Design** | Technical decisions agents must respect |
| **Standards** | Coding conventions, patterns, constraints |
| **Task List** | The canonical list of work to be done — defined by humans only |

The task list is the only source of work. **No agent may create a new task directly.** Task creation flows exclusively through the amendment process (see §1.4).

### 1.4 Amendment Flow
Agents may propose amendments to any input document at any time. Amendments follow a structured lifecycle:

```
Agent proposes amendment
        │
        ▼
Review process assesses:
  - Scope   (what is affected?)
  - Impact  (what downstream work is touched?)
  - Risk    (what could go wrong?)
        │
        ▼
Amendment packaged as a Document PR
        │
        ▼
Human reviews and merges or rejects
```

Amendments do not take effect until the Document PR is merged by a human. If an amendment affects pending tasks, those tasks remain in their current state until the amendment is resolved — the orchestrator does not act on proposed changes.

### 1.5 Task Splitting
If an agent determines mid-execution that a task is larger than planned and cannot be completed in a single PR, it:
1. Opens a **Code PR** for the work completed so far
2. Opens a **Document PR** proposing a new task (or tasks) to cover the remaining work

The agent does not abandon the task — it delivers partial value immediately and signals that more work is needed through the standard amendment channel.

---

## Layer 2 — Coordination Layer

The orchestrator manages the lifecycle of all tasks and agents. It is the sole authority over task state — agents signal intent but do not change state directly.

### 2.1 Task Lifecycle

Tasks are loaded from the validated input docs at startup and tracked through a well-defined set of states:

```
pending → in_progress → in_review ─── approved & merged ──→ done
    ↑           ↑            │
blocked ──→ pending          └── changes requested ──→ in_progress
    │
  failed  (human intervention required)
```

| Status | Meaning |
|--------|---------|
| `pending` | Ready to be claimed by an agent |
| `in_progress` | Claimed and actively being worked on |
| `in_review` | PR opened; awaiting human review |
| `done` | PR approved and merged |
| `blocked` | Has one or more unresolved dependencies |
| `failed` | Agent could not complete the task; human must intervene |

A task is not considered `done` until its PR is approved and merged. Tasks in `in_review` are still live — the agent remains attached and will resume work if the reviewer requests changes.

Dependent tasks only unblock when their dependency reaches `done`. A dependency in `in_review` is not sufficient — the work must be merged before downstream tasks can safely build on it.

### 2.2 Dependency Graph

Each task may declare dependencies on other tasks using explicit task IDs. The orchestrator builds a dependency graph from the task list at startup and uses it to:

- Determine which tasks are initially `pending` vs. `blocked`
- Promote tasks from `blocked` → `pending` as their dependencies reach `done`
- Compute the critical path for assignment prioritization (see §2.3)

Circular dependency detection is the responsibility of **Layer 1 validation** — the orchestrator assumes the graph is acyclic by the time it receives it.

### 2.3 Agent Assignment

The orchestrator maintains a fixed-size agent pool, configured by the human at startup. When a slot is free and one or more `pending` tasks exist, the orchestrator assigns the next task using a **critical path (unblocking-first)** strategy:

- The orchestrator scores each `pending` task by how many other tasks (directly and transitively) depend on it
- The task with the highest score is assigned first
- In the case of a tie, document order is used as a tiebreaker

The critical path is recomputed each time a task completes and new tasks become `pending`.

### 2.4 Failure Handling

When an agent reports a task as `failed`, the orchestrator:

1. Records the failure reason as reported by the agent
2. Marks the task as `failed`
3. Marks all tasks that depend on it as `blocked`
4. Notifies the human

No automatic retry occurs. The human must review the failure reason, take corrective action (amend input docs, rewrite the task, etc.), and manually re-queue the task. This ensures failures are understood before more resources are spent.

### 2.5 Stalled Agent Detection

Each task has a configurable time threshold. If an agent holds a task `in_progress` beyond this threshold without completing or failing, the orchestrator auto-terminates the agent and marks the task as `failed`. The standard failure handling flow (§2.4) then applies.

### 2.6 Health Monitoring

The orchestrator continuously monitors for:

- **Deadlock** — all remaining tasks are `blocked` or `failed` with nothing `in_progress`; no progress is possible without human intervention
- **Stalled agents** — an agent has exceeded its time threshold (see §2.5)
- **Queue exhaustion** — all tasks are `done`; the run is complete

### 2.7 Resumability

The system supports worktree-based resumability. The agent's worktree is its persistent workspace for the entire lifetime of a task — from initial claim through PR review and merge. It is never discarded until the task reaches `done` or `failed`.

On restart after any interruption, the orchestrator recovers state from two sources:

**PRs (remote)** — the authoritative record of completed and in-review work:
- Tasks with a merged PR are marked `done`
- Tasks with an open PR are marked `in_review`; their agent is re-spawned on the existing worktree with the PR comments as additional context

**Worktrees (local)** — the authoritative record of in-progress work:
- Tasks with an existing worktree but no PR are marked `in_progress`; their agent is re-spawned on the existing worktree and resumes from its current state, including any uncommitted changes

The task-to-worktree mapping is implicit in the filesystem — each worktree is stored at a predictable path derived from the task ID:

```
.agentic/worktrees/{task-id}/
```

On restart the orchestrator reconstructs full system state by combining two scans:

1. **Scan remote PRs** — tasks with a merged PR are `done`; tasks with an open PR are `in_review`
2. **Scan local worktrees directory** — tasks with an existing worktree but no PR are `in_progress`

Tasks not found in either scan are treated as `pending` or `blocked` based on the dependency graph.

**Agent context on resume** — when re-spawned on an existing worktree, the agent infers context from the current file state and the git diff of changes already made against the base branch. No separate state file is maintained; the code itself is the record.

**Partial corruption** — if a crash left the worktree in an inconsistent state, the agent attempts autonomous recovery by reasoning about what it finds. If recovery is not feasible, the task follows the standard failure flow and human intervention is required.

Worktrees are retained for the entire lifetime of a task — from initial claim through PR review and merge. A worktree is only cleaned up once its task reaches `done` or `failed`.

---

## Layer 3 — Execution Layer

### 3.1 Agent Isolation

Each agent works in a fully isolated environment:
- A dedicated git worktree at `.agentic/worktrees/{task-id}/`
- One agent per task, one task per agent
- Agents do not share a working directory
- Agents do not communicate with each other directly — all coordination is structural, expressed through the dependency graph in the task list

### 3.2 Agent Lifecycle

```
Claim task
    │
    ▼
Build initial context (task description + input docs)
    │
    ▼
┌─── Execution loop ──────────────────────────────────────┐
│  read code → make changes → verify → fix failures → ... │
│                                                          │
│  (agent may propose amendments at any point)            │
└──────────────────────────────┬──────────────────────────┘
                               │ verification passes
                               ▼
                          Open Code PR → task: in_review
                               │
                    ┌──────────┴──────────┐
                    │                     │
             Changes requested      Approved & merged
                    │                     │
             Reset context           Clean up worktree
             (summary: diff               │
              + review comments)     task: done
                    │
             ┌─── Execution loop ───┐
             │  address feedback    │
             └──────────┬───────────┘
                        │ verification passes
                        ▼
                   Push updates → task: in_review
                   (cycle repeats until merged)
```

The agent is fully autonomous from task claim to PR merge. No human checkpoints exist within this lifecycle.

### 3.3 Agent Capabilities

During its execution loop the agent has four categories of capabilities:

| Category | Examples |
|----------|---------|
| **Read context** | Read input docs, read existing code, search the codebase |
| **Make changes** | Write files, create files, delete files |
| **Verify work** | Run tests, run linters, run build commands |
| **Coordinate** | Signal task completion, propose amendments, signal blockage |

### 3.4 Verification

Verification is mandatory before opening or updating a PR. The agent must run all relevant checks (tests, linters, build) and all checks must pass before a PR is opened or updated.

If verification fails the agent remains in the execution loop, diagnoses the failure, and addresses it. If the agent cannot resolve the failures it marks the task `failed` and human intervention is required.

### 3.5 Context Window Management

The agent uses milestone-based context resets to stay coherent across long-running tasks. At each major lifecycle transition the agent discards its raw conversation history and rebuilds a fresh, focused context from durable artifacts:

| Milestone | Context reset content |
|-----------|----------------------|
| Task start | Task description + input docs |
| PR opened | Summary of what was built + git diff |
| Changes requested | Summary of previous work + PR review comments |

This keeps each phase of work focused without carrying an ever-growing history. The git diff and PR comments are always available as ground truth so no information is permanently lost.

### 3.6 Amendment Proposals

At any point during execution an agent may determine that an input document needs to change. The agent classifies each proposed amendment:

- **Non-critical** — the agent proposes the Document PR and continues working, documenting any assumptions it makes in the PR description
- **Critical** — the agent proposes the Document PR and pauses, waiting for the amendment to be merged before continuing; the agent documents why it considers the amendment blocking

In both cases the amendment follows the flow defined in §1.4.

### 3.7 Task Splitting

If an agent determines mid-execution that the task is larger than planned it:
1. Completes what can be done within the current scope and opens a Code PR
2. Opens a Document PR proposing a new task for the remaining work

The current task proceeds to `in_review` normally. The new task enters the queue only after the Document PR is merged.

### 3.8 PR Contract

Every PR maps 1-to-1 with a single task. This is a hard constraint.

| PR Type | Triggered by | Contains |
|---------|-------------|---------|
| **Code PR** | Task completion | Code changes for that task |
| **Document PR** | Agent amendment proposal | Changes to one or more input docs |

Both PR types go through the same human review process. The difference is in content and intent, not mechanism.

---

## System Diagram

```
┌─────────────────────────────────────────────────────────┐
│                      INPUT LAYER                         │
│  [Scaffolding]  [Validation]  [Docs + Task List]        │
│                        ↑                                 │
│              Amendment PRs (human-approved)              │
└─────────────────────┬───────────────────────────────────┘
                      │ validated task list
┌─────────────────────▼───────────────────────────────────┐
│                  COORDINATION LAYER                      │
│         [Task Queue]  [Dependency Graph]                 │
│         [Monitoring]  [Agent Assignment]                 │
└──────┬──────────┬──────────┬──────────┬─────────────────┘
       │          │          │          │  (one task each)
┌──────▼──┐ ┌────▼────┐ ┌───▼─────┐ ┌──▼──────┐
│ Agent 1 │ │ Agent 2 │ │ Agent 3 │ │ Agent N │   EXECUTION
│worktree1│ │worktree2│ │worktree3│ │worktreeN│    LAYER
└──────┬──┘ └────┬────┘ └───┬─────┘ └──┬──────┘
       │         │          │           │
       └─────────┴──────────┴───────────┘
                            │
                     [Pull Requests]
                            │
                    [Human Review]
```

---

## Amendment Concurrency

If two agents simultaneously propose conflicting amendments to the same document, both Document PRs are opened independently. The human reviewer resolves any conflicts during review, the same way code merge conflicts are handled in any git-based workflow. No special system mechanism is required — the human is already the final authority on all amendments.
