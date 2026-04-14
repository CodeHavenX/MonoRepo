# Amendment: Local VCS Provider

> Status: Implemented
> Amends: TECH_DESIGN.md §VcsProvider, ARCHITECTURE.md

---

## Motivation

The existing `VcsProvider` abstraction has a single implementation (`GitHubVcsProvider`) that requires network access to the GitHub API via the `gh` CLI. This prevents fully offline workflows and makes local development and testing without a GitHub remote impractical.

This amendment adds a `LocalVcsProvider` that models pull requests as local Git branch merges, with all PR state persisted to a JSON file on disk. No network access is required.

---

## New Package: `vcs/local/`

```
vcs/local/
  LocalVcsState.kt     # Serializable data models for the JSON state file
  LocalVcsProvider.kt  # VcsProvider implementation
```

### `vcs/local/LocalVcsState.kt`

Defines the on-disk representation persisted to `.agentic/local_prs.json` (configurable):

```kotlin
@Serializable
data class LocalPrState(
    val prs: MutableList<LocalPr> = mutableListOf(),
    var nextPrId: Int = 1,
)

@Serializable
data class LocalPr(
    val id: String,
    val sourceBranch: String,
    val targetBranch: String,
    val title: String,
    val body: String,
    var state: PullRequestState,
    val labels: List<String>,
    val comments: MutableList<LocalComment> = mutableListOf(),
    var hasRequestedChanges: Boolean = false,
)

@Serializable
data class LocalComment(val author: String, val body: String, val createdAtEpochMs: Long)
```

### `vcs/local/LocalVcsProvider.kt`

Implements `VcsProvider`. All state is read from and written to the JSON state file on every operation. A `Mutex` guards concurrent access. Git operations are executed via the existing `ShellRunner`.

| Method | Behavior |
|--------|----------|
| `createPullRequest` | Validates no open PR exists for the branch; appends a new `LocalPr`; if `autoMerge=true` runs `git merge --no-ff <source>` and sets state to `MERGED`; throws `VcsProviderException` on git failure |
| `listOpenPullRequests(labels)` | Reads state file; filters `state == OPEN` and matching labels |
| `listMergedPullRequests(labels)` | Reads state file; filters `state == MERGED` and matching labels |
| `isPullRequestMerged(prId)` | If already `MERGED` in state, returns `true`; for `autoMerge=false` runs `git branch --merged <target>` and lazily updates state on match; throws `VcsProviderException` on git failure |
| `getPullRequestComments(prId)` | Returns `comments` from the state file entry; throws `IllegalArgumentException` for unknown IDs |
| `addPullRequestComment(prId, body)` | Appends a `LocalComment`; throws `IllegalArgumentException` for unknown IDs |
| `pullRequestHasRequestedChanges(prId)` | Returns `hasRequestedChanges` flag; throws `IllegalArgumentException` for unknown IDs |

**Merge modes:**

- `autoMerge = false` (default): PRs are recorded as `OPEN`. The caller (typically a human running `git merge`) is responsible for merging the branch. `isPullRequestMerged` detects the merge lazily via `git branch --merged` and updates the state file.
- `autoMerge = true`: `createPullRequest` immediately runs `git merge --no-ff <sourceBranch>` and records the PR as `MERGED`. Enables a fully automated offline workflow with no human review step.

**PR URL:** Local PRs use the scheme `local://pr/<id>` as a placeholder URL (no remote required).

---

## Config Addition

`VcsProviderConfig` gains a new sealed variant:

```kotlin
@Serializable
@SerialName("local")
data class Local(
    val stateFile: String = ".agentic/local_prs.json",
    val autoMerge: Boolean = false,
) : VcsProviderConfig()
```

Example `config.json`:

```json
{
  "vcsProvider": {
    "type": "local",
    "stateFile": ".agentic/local_prs.json",
    "autoMerge": true
  }
}
```

---

## Files Changed

| File | Action | Reason |
|------|--------|--------|
| `vcs/local/LocalVcsState.kt` | **Create** | On-disk state models |
| `vcs/local/LocalVcsProvider.kt` | **Create** | Offline `VcsProvider` implementation |
| `core/AgenticConfig.kt` | **Modify** | Add `VcsProviderConfig.Local` sealed variant |
| `app/AgenticModule.kt` | **Modify** | Wire `LocalVcsProvider` in the Koin factory |

---

## Tests

New test files follow the project's three-tier convention:

| File | Tier | Coverage |
|------|------|----------|
| `vcs/local/LocalVcsProviderTest.kt` | Basic | CRUD operations, label filtering, state file creation, nested directory creation |
| `vcs/local/LocalVcsProviderExtendedTest.kt` | Extended | `autoMerge=true` git merge execution and command verification; `autoMerge=false` lazy merge detection; state persistence across separate instances; label union filtering |
| `vcs/local/LocalVcsProviderNegativeTest.kt` | Negative | Unknown `prId` on all mutating/reading methods; git merge failure throws `VcsProviderException`; duplicate open PR on same branch throws `IllegalStateException`; git `branch --merged` failure throws `VcsProviderException` |

`ShellRunner` is mocked via `mockk<ShellRunner>()`. The state file is backed by a JUnit `@TempDir`.

---

## Non-Goals

- This amendment does **not** add a CLI command to manually approve/reject a local PR (toggle `hasRequestedChanges`). That is a future enhancement.
- Conflict resolution during `autoMerge` is left to the caller; a non-zero git exit code surfaces as `VcsProviderException`.
- The existing `FakeVcsProvider` is retained for unit-test use elsewhere in the project; `LocalVcsProvider` is an offline production implementation, not a test double.
