package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus
import com.cramsan.agentic.execution.AgentResult
import com.cramsan.agentic.execution.AgentRunner
import com.cramsan.agentic.execution.WorktreeManager
import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.notification.Notifier
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "DefaultOrchestrator"

class DefaultOrchestrator(
    private val taskStore: TaskStore,
    private val stateDeriver: StateDeriver,
    private val dependencyGraph: DependencyGraph,
    private val worktreeManager: WorktreeManager,
    private val agentRunner: AgentRunner,
    private val notifier: Notifier,
) : Orchestrator {

    override suspend fun run(config: OrchestratorConfig) {
        val tasks = taskStore.getAll()
        val activeTaskIds: MutableSet<String> = ConcurrentHashMap.newKeySet()

        coroutineScope {
            while (true) {
                val statuses = deriveMemoized(tasks)

                // Termination: all done
                if (statuses.values.all { it == TaskStatus.DONE }) {
                    notifier.notify(AgenticEvent.RunCompleted(tasks))
                    return@coroutineScope
                }

                // Termination: deadlock
                val hasMakingProgress = statuses.values.any {
                    it == TaskStatus.IN_PROGRESS || it == TaskStatus.PENDING
                } || activeTaskIds.isNotEmpty()
                if (!hasMakingProgress) {
                    val blocked = tasks.filter { statuses[it] == TaskStatus.BLOCKED }
                    val failed = tasks.filter { statuses[it] == TaskStatus.FAILED }
                    notifier.notify(AgenticEvent.RunDeadlocked(blocked, failed))
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
                                        notifier.notify(AgenticEvent.TaskFailed(task, result.reason))
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
        return deriveMemoized(taskStore.getAll())
    }

    private suspend fun deriveMemoized(tasks: List<Task>): Map<Task, TaskStatus> {
        // Build statuses in topological order (tasks with no dependencies first)
        // so dependency statuses are available when evaluating downstream tasks
        val resolved = mutableMapOf<String, TaskStatus>()
        val result = mutableMapOf<Task, TaskStatus>()

        // Simple topological sort: repeatedly process tasks whose dependencies are all resolved
        val remaining = tasks.toMutableList()
        val maxIterations = tasks.size + 1
        var iteration = 0
        while (remaining.isNotEmpty() && iteration < maxIterations) {
            iteration++
            val iter = remaining.iterator()
            while (iter.hasNext()) {
                val task = iter.next()
                if (task.dependencies.all { it in resolved }) {
                    val depStatuses = task.dependencies.associateWith { resolved[it]!! }
                    val status = stateDeriver.statusOf(task, depStatuses)
                    resolved[task.id] = status
                    result[task] = status
                    iter.remove()
                }
            }
        }

        // Any remaining tasks (circular deps, shouldn't happen after validation) — just evaluate them
        for (task in remaining) {
            val status = stateDeriver.statusOf(task)
            result[task] = status
        }

        return result
    }
}
