# Agentic — Architecture Document

> Status: Draft (in progress)

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

The orchestrator manages the lifecycle of all tasks and agents.

### 2.1 Task Queue
The task queue is derived from the input document task list at startup. Each task has a status and may declare dependencies on other tasks.

| Status | Meaning |
|--------|---------|
| `pending` | Ready to be claimed |
| `in_progress` | Claimed by an agent |
| `blocked` | Waiting on a dependency |
| `done` | PR opened |
| `failed` | Agent could not complete the task |

### 2.2 Dependency Resolution
A task becomes `pending` only when all tasks it depends on reach `done`. The orchestrator manages this automatically as tasks complete.

### 2.3 Monitoring
The orchestrator continuously monitors for:
- **Deadlocks** — all remaining tasks are blocked and nothing is in progress
- **Stalled agents** — an agent has been on a task beyond a configurable threshold
- **Queue exhaustion** — all tasks are done, run is complete

---

## Layer 3 — Execution Layer

### 3.1 Agent Isolation
Each agent works in a fully isolated environment:
- A dedicated **git worktree** on its own branch
- One agent per task, one task per agent
- Agents do not share a working directory

### 3.2 Agent Lifecycle

```
Claim task from queue
        │
        ▼
Read relevant input docs for context
        │
        ▼
Autonomous execution loop
(read code, make changes, run checks, iterate)
        │
        ├─ Task fits in one PR → open Code PR → mark done
        │
        └─ Task too large → open Code PR (partial) + Document PR (new task) → mark done
```

The agent is fully autonomous from task claim to PR submission. No human checkpoints exist within this lifecycle.

### 3.3 PR Contract
Every PR maps 1-to-1 with a single task. This is a hard constraint.

| PR Type | Triggered by | Contains |
|---------|-------------|---------|
| **Code PR** | Task completion | Code changes for that task |
| **Document PR** | Agent amendment proposal | Changes to one or more input docs |

Both PR types go through the same human review process. The system does not distinguish them mechanically — the difference is in content and intent.

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

## Open Questions

1. **Amendment concurrency**: If two agents simultaneously propose conflicting amendments to the same document, how is that handled?

2. **Failed tasks**: If an agent marks a task as `failed`, does a human need to intervene, or does the orchestrator retry with a new agent automatically?

3. **Run resumability**: If the process is interrupted mid-run (crash, manual stop), should the orchestrator be able to resume from where it left off, or does it restart from scratch?
