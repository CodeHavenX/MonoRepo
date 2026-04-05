package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI

private const val TAG = "DefaultDependencyGraph"

class DefaultDependencyGraph(private val tasks: List<Task>) : DependencyGraph {

    // dependents[taskId] = set of task IDs that directly depend on taskId
    private val dependents: Map<String, Set<String>> = buildDependentsMap()

    override fun downstreamCount(taskId: String): Int {
        logD(TAG, "downstreamCount called: taskId=$taskId")
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
        val count = visited.size
        logD(TAG, "downstreamCount result: taskId=$taskId, count=$count")
        return count
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
}
