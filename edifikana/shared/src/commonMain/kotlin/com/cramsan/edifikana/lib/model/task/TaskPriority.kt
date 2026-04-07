package com.cramsan.edifikana.lib.model.task


import kotlinx.serialization.Serializable

/**
 * Represents the priority level of a task.
 */
@Serializable
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
}
