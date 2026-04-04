package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task

class DefaultDependencyGraph(private val tasks: List<Task>) : DependencyGraph {

    // dependents[taskId] = set of task IDs that directly depend on taskId
    private val dependents: Map<String, Set<String>> = buildDependentsMap()

    override fun downstreamCount(taskId: String): Int {
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.add(taskId)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val deps = dependents[current] ?: emptySet()
            for (dep in deps) {
                if (dep !in visited) {
                    visited.add(dep)
                    queue.add(dep)
                }
            }
        }
        return visited.size
    }

    private fun buildDependentsMap(): Map<String, Set<String>> {
        val map = mutableMapOf<String, MutableSet<String>>()
        for (task in tasks) {
            for (depId in task.dependencies) {
                map.getOrPut(depId) { mutableSetOf() }.add(task.id)
            }
        }
        return map
    }
}
