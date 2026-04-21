package com.cramsan.agentic.coordination

/** Provides dependency graph queries for task scheduling decisions. */
interface DependencyGraph {
    /** Returns the total number of tasks transitively downstream of [taskId] (used for critical-path ordering). */
    fun downstreamCount(taskId: String): Int

    /** Returns the set of task IDs that directly depend on [taskId] (used for Kahn's topological sort). */
    fun dependentsOf(taskId: String): Set<String>
}
