package com.cramsan.edifikana.lib.model.task



import kotlinx.serialization.Serializable

/**
 * Represents the lifecycle status of a task.
 */
@Serializable
enum class TaskStatus {
    OPEN,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
}
