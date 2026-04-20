package com.cramsan.agentic.coordination

interface DependencyGraph {
    fun downstreamCount(taskId: String): Int
    fun dependentsOf(taskId: String): Set<String>
}
