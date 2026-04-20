package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus
import com.cramsan.agentic.execution.AgentResult
import com.cramsan.agentic.execution.AgentRunner
import com.cramsan.agentic.execution.WorktreeManager
import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.notification.Notifier
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "DefaultOrchestrator"

class DefaultOrchestrator(
    private val taskListProvider: TaskListProvider,
    private val stateDeriver: StateDeriver,
    private val dependencyGraph: DependencyGraph,
    private val worktreeManager: WorktreeManager,
    private val agentRunner: AgentRunner,
    private val notifier: Notifier,
) : Orchestrator {

    private val cleanedUpTaskIds: MutableSet<String> = mutableSetOf()

    override suspend fun run(config: OrchestratorConfig) {
        val tasks = taskListProvider.provide()
        val activeTaskIds: MutableSet<String> = ConcurrentHashMap.newKeySet()

        coroutineScope {
            while (true) {
                val statuses = deriveMemoized(tasks)

                // Clean up worktrees for completed/failed tasks
                statuses.entries
                    .filter { (task, status) ->
                        (status == TaskStatus.DONE || status == TaskStatus.FAILED) &&
                            task.id !in cleanedUpTaskIds
                    }
                    .forEach { (task, _) ->
                        try {
                            worktreeManager.delete(task.id)
                            cleanedUpTaskIds += task.id
                        } catch (e: Exception) {
                            logW(TAG, "Failed to clean up worktree for task ${task.id}: ${e.message}")
                        }
                    }

                // Termination: all done
                if (statuses.values.all { it == TaskStatus.DONE }) {
                    try {
                        notifier.notify(AgenticEvent.RunCompleted(tasks))
                    } catch (e: Exception) {
                        logW(TAG, "Notifier failed during RunCompleted: ${e.message}")
                    }
                    return@coroutineScope
                }

                // Termination: deadlock
                val hasMakingProgress = statuses.values.any {
                    it == TaskStatus.IN_PROGRESS || it == TaskStatus.PENDING
                } || activeTaskIds.isNotEmpty()
                if (!hasMakingProgress) {
                    val blocked = tasks.filter { statuses[it] == TaskStatus.BLOCKED }
                    val failed = tasks.filter { statuses[it] == TaskStatus.FAILED }
                    try {
                        notifier.notify(AgenticEvent.RunDeadlocked(blocked, failed))
                    } catch (e: Exception) {
                        logW(TAG, "Notifier failed during RunDeadlocked: ${e.message}")
                    }
                    return@coroutineScope
                }

                // Launch agents
                val freeSlots = config.agentPoolSize - activeTaskIds.size
                if (freeSlots > 0) {
                    statuses.entries
                        .filter { (task, status) ->
                            (status == TaskStatus.PENDING || status == TaskStatus.IN_PROGRESS) &&
                                task.id !in activeTaskIds
                        }
                        .sortedByDescending { (task, _) -> dependencyGraph.downstreamCount(task.id) }
                        .take(freeSlots)
                        .forEach { (task, _) ->
                            val worktree = worktreeManager.getOrCreate(task.id)
                            activeTaskIds += task.id
                            logI(TAG, "Launching agent for task ${task.id}")
                            launch(Dispatchers.IO) {
                                try {
                                    val result = agentRunner.run(task, worktree)
                                    if (result is AgentResult.Failed) {
                                        try {
                                            notifier.notify(AgenticEvent.TaskFailed(task, result.reason))
                                        } catch (e: Exception) {
                                            logW(TAG, "Notifier failed during TaskFailed for ${task.id}: ${e.message}")
                                        }
                                    }
                                } finally {
                                    activeTaskIds -= task.id
                                }
                            }
                        }
                }

                delay(config.pollIntervalSeconds * 1_000L)
            }
        }
    }

    override suspend fun status(): Map<Task, TaskStatus> {
        return deriveMemoized(taskListProvider.provide())
    }

    private suspend fun deriveMemoized(tasks: List<Task>): Map<Task, TaskStatus> {
        val prContext = stateDeriver.fetchPrContext()
        val taskById = tasks.associateBy { it.id }

        // Kahn's algorithm: process tasks in topological order so each task's
        // dependency statuses are already resolved before it is evaluated.
        val inDegree = tasks.associate { it.id to it.dependencies.size }.toMutableMap()
        val queue = ArrayDeque(tasks.filter { it.dependencies.isEmpty() })
        val resolved = mutableMapOf<String, TaskStatus>()
        val result = LinkedHashMap<Task, TaskStatus>()

        while (queue.isNotEmpty()) {
            val task = queue.removeFirst()
            val depStatuses = task.dependencies.associateWith { resolved[it]!! }
            val status = try {
                stateDeriver.statusOf(task, depStatuses, prContext)
            } catch (e: Exception) {
                logW(TAG, "Failed to derive status for task ${task.id}: ${e.message}. Treating as PENDING.")
                TaskStatus.PENDING
            }
            resolved[task.id] = status
            result[task] = status

            for (dependentId in dependencyGraph.dependentsOf(task.id)) {
                if ((inDegree.merge(dependentId, -1, Int::plus) ?: 0) == 0) {
                    taskById[dependentId]?.let { queue += it }
                }
            }
        }

        // Any tasks not reached have a dependency cycle — evaluate without resolved deps.
        if (result.size < tasks.size) {
            val cyclic = tasks.filter { it.id !in resolved }
            logW(TAG, "Dependency cycle detected among tasks: ${cyclic.map { it.id }}. Evaluating without dependency context.")
            for (task in cyclic) {
                val status = try {
                    stateDeriver.statusOf(task, prContext = prContext)
                } catch (e: Exception) {
                    logW(TAG, "Failed to derive status for task ${task.id}: ${e.message}. Treating as PENDING.")
                    TaskStatus.PENDING
                }
                result[task] = status
            }
        }

        return result
    }
}
