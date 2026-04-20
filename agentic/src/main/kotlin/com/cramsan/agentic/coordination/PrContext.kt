package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.PullRequest

data class PrContext(
    val mergedPrs: List<PullRequest>,
    val openPrs: List<PullRequest>,
)
