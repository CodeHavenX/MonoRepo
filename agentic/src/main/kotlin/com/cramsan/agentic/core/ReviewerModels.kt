package com.cramsan.agentic.core

// Not serialized; loaded from raw file content
data class ReviewerDefinition(
    val name: String,
    val systemPrompt: String,
)

data class ReviewerFeedback(
    val reviewerName: String,
    val content: String,
)
