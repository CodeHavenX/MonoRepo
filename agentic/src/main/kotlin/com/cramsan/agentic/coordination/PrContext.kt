package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.PullRequest

/**
 * A snapshot of VCS pull-request state fetched once per orchestrator tick by
 * [com.cramsan.agentic.coordination.StateDeriver.fetchPrContext].
 *
 * Both lists are filtered to PRs carrying the `agentic-code` label, so they contain only
 * PRs created by agents — not unrelated human PRs in the same repository.
 *
 * This snapshot is intentionally immutable within a tick: if an agent merges a PR while
 * [com.cramsan.agentic.coordination.DefaultOrchestrator.deriveMemoized] is running, the change
 * will only be reflected on the **next** poll tick. This is expected and does not cause
 * correctness issues — at worst it delays DONE detection by one poll interval.
 */
data class PrContext(
    val mergedPrs: List<PullRequest>,
    val openPrs: List<PullRequest>,
)
