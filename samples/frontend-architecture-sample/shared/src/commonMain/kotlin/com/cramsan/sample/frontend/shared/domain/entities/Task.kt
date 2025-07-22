package com.cramsan.sample.frontend.shared.domain.entities

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Core domain entity representing a task in the task management system.
 * This entity contains only business logic and no UI or platform-specific code.
 */
@Serializable
data class Task(
    val id: TaskId,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val createdAt: Instant,
    val completedAt: Instant? = null,
    val dueDate: Instant? = null,
) {
    /**
     * Business rule: A task can only be marked as completed if it has a title
     */
    fun canBeCompleted(): Boolean = title.isNotBlank()
    
    /**
     * Business rule: Check if task is overdue
     */
    fun isOverdue(currentTime: Instant): Boolean {
        return dueDate?.let { due ->
            !isCompleted && currentTime > due
        } ?: false
    }
}

/**
 * Value object for task identifier
 */
@Serializable
@JvmInline
value class TaskId(val value: String)

/**
 * Task priority levels following business requirements
 */
@Serializable
enum class TaskPriority(val displayName: String, val sortOrder: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4);
    
    companion object {
        fun fromDisplayName(displayName: String): TaskPriority? {
            return entries.find { it.displayName == displayName }
        }
    }
}