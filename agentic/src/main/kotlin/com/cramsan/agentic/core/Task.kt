package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val dependencies: List<String>,
    val timeoutSeconds: Long = 3600L,
)

// NOT @Serializable — status is always derived, never persisted
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    IN_REVIEW,
    DONE,
    BLOCKED,
    FAILED,
}
