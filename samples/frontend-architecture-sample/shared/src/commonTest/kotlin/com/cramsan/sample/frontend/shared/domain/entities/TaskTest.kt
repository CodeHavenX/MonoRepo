package com.cramsan.sample.frontend.shared.domain.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days

class TaskTest {
    
    @Test
    fun canBeCompleted_withValidTitle_returnsTrue() {
        val task = createTestTask(title = "Valid title")
        assertTrue(task.canBeCompleted())
    }
    
    @Test
    fun canBeCompleted_withBlankTitle_returnsFalse() {
        val task = createTestTask(title = "")
        assertFalse(task.canBeCompleted())
        
        val taskWithSpaces = createTestTask(title = "   ")
        assertFalse(taskWithSpaces.canBeCompleted())
    }
    
    @Test
    fun isOverdue_withPastDueDateAndNotCompleted_returnsTrue() {
        val now = Clock.System.now()
        val pastDue = now - 1.days
        val task = createTestTask(dueDate = pastDue, isCompleted = false)
        
        assertTrue(task.isOverdue(now))
    }
    
    @Test
    fun isOverdue_withPastDueDateButCompleted_returnsFalse() {
        val now = Clock.System.now()
        val pastDue = now - 1.days
        val task = createTestTask(dueDate = pastDue, isCompleted = true)
        
        assertFalse(task.isOverdue(now))
    }
    
    @Test
    fun isOverdue_withFutureDueDate_returnsFalse() {
        val now = Clock.System.now()
        val futureDue = now + 1.days
        val task = createTestTask(dueDate = futureDue, isCompleted = false)
        
        assertFalse(task.isOverdue(now))
    }
    
    @Test
    fun isOverdue_withNoDueDate_returnsFalse() {
        val now = Clock.System.now()
        val task = createTestTask(dueDate = null, isCompleted = false)
        
        assertFalse(task.isOverdue(now))
    }
    
    private fun createTestTask(
        title: String = "Test Task",
        description: String = "Test Description",
        isCompleted: Boolean = false,
        dueDate: Instant? = null
    ): Task {
        return Task(
            id = TaskId("test_id"),
            title = title,
            description = description,
            isCompleted = isCompleted,
            priority = TaskPriority.MEDIUM,
            createdAt = Clock.System.now(),
            dueDate = dueDate
        )
    }
}