package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task

interface TaskStore {
    fun getAll(): List<Task>
    fun get(id: String): Task
}
