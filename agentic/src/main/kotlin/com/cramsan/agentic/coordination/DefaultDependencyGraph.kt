package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI

private const val TAG = "DefaultDependencyGraph"

class DefaultDependencyGraph(private val tasks: List<Task>) : DependencyGraph {

    // dependents[taskId] = set of task IDs that directly depend on taskId
    private val dependents: Map<String, Set<String>> = buildDependentsMap()

    // Precomputed BFS result for every task ID — immutable after construction
    private val downstreamCounts: Map<String, Int> = buildDownstreamCounts()

    override fun downstreamCount(taskId: String): Int {
        return downstreamCounts[taskId] ?: 0
    }

    override fun dependentsOf(taskId: String): Set<String> {
        return dependents[taskId] ?: emptySet()
    }

    private fun buildDependentsMap(): Map<String, Set<String>> {
        logD(TAG, "Building dependents map for ${tasks.size} tasks")
        val map = mutableMapOf<String, MutableSet<String>>()
        var edgeCount = 0
        for (task in tasks) {
            for (depId in task.dependencies) {
                map.getOrPut(depId) { mutableSetOf() }.add(task.id)
                edgeCount++
            }
        }
        logI(TAG, "Dependency graph constructed: ${tasks.size} tasks, $edgeCount edges")
        return map
    }

    private fun buildDownstreamCounts(): Map<String, Int> {
        val allIds = mutableSetOf<String>()
        for (task in tasks) {
            allIds.add(task.id)
            allIds.addAll(task.dependencies)
        }

        val result = mutableMapOf<String, Int>()
        for (id in allIds) {
            val visited = mutableSetOf<String>()
            val queue = ArrayDeque<String>()
            queue.add(id)
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
            result[id] = visited.size
        }
        logI(TAG, "Precomputed downstream counts for ${result.size} nodes")
        return result
    }
}
