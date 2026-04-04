package com.cramsan.agentic.reviewer

import com.cramsan.agentic.core.ReviewerDefinition

interface ReviewerLoader {
    fun loadAll(): List<ReviewerDefinition>
}
