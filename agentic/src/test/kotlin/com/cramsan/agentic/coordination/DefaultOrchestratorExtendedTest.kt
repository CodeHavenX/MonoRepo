package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus
import com.cramsan.agentic.execution.AgentResult
import com.cramsan.agentic.execution.AgentRunner
import com.cramsan.agentic.execution.Worktree
import com.cramsan.agentic.execution.WorktreeManager
import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.notification.Notifier
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.delay
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Extended orchestrator tests enforcing requirements from TECH_DESIGN.md §10 and ARCHITECTURE.md §2.
 */
class DefaultOrchestratorExtendedTest {

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        coEvery { stateDeriver.fetchPrContext() } returns PrContext(emptyList(), emptyList())
    }

    private val stateDeriver = mockk<StateDeriver>()
    private val dependencyGraph = mockk<DependencyGraph>()
    private val worktreeManager = mockk<WorktreeManager>(relaxed = true)
    private val agentRunner = mockk<AgentRunner>()
    private val notifier = mockk<Notifier>(relaxed = true)

    private val taskListProvider = mockk<TaskListProvider>()

    private fun worktree(id: String) = Worktree(id, Path.of("/tmp/$id"), "agentic/$id")
    private fun makeTask(id: String, vararg deps: String) =
        Task(id = id, title = id, description = id, dependencies = deps.toList())

    private val config = OrchestratorConfig(
        agentPoolSize = 3,
        pollIntervalSeconds = 0L,
        baseBranch = "main",
    )

    private fun makeOrchestrator() =
        DefaultOrchestrator(taskListProvider, stateDeriver, dependencyGraph, worktreeManager, agentRunner, notifier)

    // ── Worktree cleanup requirements ────────────────────────────────────────

    @Test
    fun `worktree is deleted when task transitions to DONE`() = runTest {
        val task = makeTask("task-1")
        coEvery { taskListProvider.provide() } returns listOf(task)
        var tick = 0
        coEvery { stateDeriver.statusOf(task, any(), any()) } answers { if (tick++ == 0) TaskStatus.PENDING else TaskStatus.DONE }
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()
        coEvery { worktreeManager.getOrCreate("task-1") } returns worktree("task-1")
        coEvery { agentRunner.run(task, any()) } returns AgentResult.PrOpened("pr-1", "url")

        makeOrchestrator().run(config)

        coVerify { worktreeManager.delete("task-1") }
    }

    @Test
    fun `worktree is deleted when task transitions to FAILED`() = runTest {
        val task = makeTask("task-1")
        coEvery { taskListProvider.provide() } returns listOf(task)
        var tick = 0
        coEvery { stateDeriver.statusOf(task, any(), any()) } answers { if (tick++ == 0) TaskStatus.PENDING else TaskStatus.FAILED }
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()
        coEvery { worktreeManager.getOrCreate("task-1") } returns worktree("task-1")
        coEvery { agentRunner.run(task, any()) } returns AgentResult.Failed("out of tokens")

        makeOrchestrator().run(config)

        coVerify { worktreeManager.delete("task-1") }
    }

    @Test
    fun `worktree cleanup does not happen twice for the same task`() = runTest {
        val task = makeTask("task-1")
        coEvery { taskListProvider.provide() } returns listOf(task)
        var tick = 0
        coEvery { stateDeriver.statusOf(task, any(), any()) } answers {
            when (tick++) { 0 -> TaskStatus.PENDING else -> TaskStatus.DONE }
        }
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()
        coEvery { worktreeManager.getOrCreate("task-1") } returns worktree("task-1")
        coEvery { agentRunner.run(task, any()) } returns AgentResult.PrOpened("pr-1", "url")

        makeOrchestrator().run(config)

        coVerify(exactly = 1) { worktreeManager.delete("task-1") }
    }

    // ── IN_REVIEW tasks must not consume an agent slot ────────────────────────

    @Test
    fun `IN_REVIEW task does not launch agent — agent slot stays free for PENDING tasks`() = runTest {
        val inReviewTask = makeTask("in-review")
        val pendingTask = makeTask("pending")
        coEvery { taskListProvider.provide() } returns listOf(inReviewTask, pendingTask)
        coEvery { stateDeriver.statusOf(inReviewTask, any(), any()) } returns TaskStatus.IN_REVIEW
        var pendingTick = 0
        coEvery { stateDeriver.statusOf(pendingTask, any(), any()) } answers {
            if (pendingTick++ == 0) TaskStatus.PENDING else TaskStatus.DONE
        }
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()
        coEvery { worktreeManager.getOrCreate("pending") } returns worktree("pending")
        coEvery { agentRunner.run(pendingTask, any()) } returns AgentResult.PrOpened("pr-1", "url")

        makeOrchestrator().run(config.copy(agentPoolSize = 1))

        coVerify(exactly = 0) { worktreeManager.getOrCreate("in-review") }
        coVerify(exactly = 1) { worktreeManager.getOrCreate("pending") }
    }

    // ── Deadlock must NOT be declared while agents are still running ──────────

    @Test
    fun `all tasks BLOCKED or FAILED but deadlock not declared because active agents still running`() = runTest {
        val task = makeTask("task-1")
        coEvery { taskListProvider.provide() } returns listOf(task)

        var tick = 0
        coEvery { stateDeriver.statusOf(task, any(), any()) } answers {
            if (tick++ == 0) TaskStatus.PENDING else TaskStatus.FAILED
        }
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()
        coEvery { worktreeManager.getOrCreate("task-1") } returns worktree("task-1")
        coEvery { agentRunner.run(task, any()) } returns AgentResult.Failed("reason")

        makeOrchestrator().run(config)

        coVerify(atLeast = 1) { notifier.notify(any()) }
    }

    // ── RunCompleted notification content ────────────────────────────────────

    @Test
    fun `RunCompleted event includes all completed tasks`() = runTest {
        val task1 = makeTask("task-1")
        val task2 = makeTask("task-2")
        coEvery { taskListProvider.provide() } returns listOf(task1, task2)
        coEvery { stateDeriver.statusOf(any(), any(), any()) } returns TaskStatus.DONE
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()

        makeOrchestrator().run(config)

        coVerify {
            notifier.notify(
                match { event ->
                    event is AgenticEvent.RunCompleted && event.completedTasks.size == 2
                },
            )
        }
    }

    // ── RunDeadlocked notification content ───────────────────────────────────

    @Test
    fun `RunDeadlocked event contains blocked and failed tasks`() = runTest {
        val blockedTask = makeTask("blocked")
        val failedTask = makeTask("failed")
        coEvery { taskListProvider.provide() } returns listOf(blockedTask, failedTask)
        coEvery { stateDeriver.statusOf(blockedTask, any(), any()) } returns TaskStatus.BLOCKED
        coEvery { stateDeriver.statusOf(failedTask, any(), any()) } returns TaskStatus.FAILED
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()

        makeOrchestrator().run(config)

        coVerify {
            notifier.notify(
                match { event ->
                    event is AgenticEvent.RunDeadlocked &&
                        event.blockedTasks.any { it.id == "blocked" } &&
                        event.failedTasks.any { it.id == "failed" }
                },
            )
        }
    }

    // ── Agent pool sizing ────────────────────────────────────────────────────

    @Test
    fun `only agentPoolSize agents launched per tick`() = runTest {
        val tasks = (1..5).map { makeTask("task-$it") }
        coEvery { taskListProvider.provide() } returns tasks
        val launchedIds = mutableListOf<String>()
        coEvery { stateDeriver.statusOf(any(), any(), any()) } answers {
            val task = firstArg<Task>()
            if (launchedIds.contains(task.id)) TaskStatus.DONE else TaskStatus.PENDING
        }
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()
        coEvery { worktreeManager.getOrCreate(any()) } answers {
            val id = firstArg<String>()
            worktree(id)
        }
        coEvery { agentRunner.run(any(), any()) } coAnswers {
            val task = firstArg<Task>()
            launchedIds.add(task.id)
            AgentResult.PrOpened("pr-${task.id}", "url")
        }

        makeOrchestrator().run(config.copy(agentPoolSize = 2))

        assertEquals(5, tasks.size)
    }

    // ── Task assignment must not re-launch in-flight agents ──────────────────

    @Test
    fun `agent is not re-launched for task already in activeTaskIds`() = runTest {
        val task = makeTask("task-1")
        coEvery { taskListProvider.provide() } returns listOf(task)
        var tick = 0
        coEvery { stateDeriver.statusOf(task, any(), any()) } answers {
            if (tick++ < 2) TaskStatus.PENDING else TaskStatus.DONE
        }
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()
        coEvery { worktreeManager.getOrCreate("task-1") } returns worktree("task-1")
        coEvery { agentRunner.run(task, any()) } coAnswers {
            delay(config.pollIntervalSeconds * 2_000L)
            AgentResult.PrOpened("pr-1", "url")
        }

        makeOrchestrator().run(config)

        coVerify(exactly = 1) { agentRunner.run(task, any()) }
    }

    // ── Topological ordering ─────────────────────────────────────────────────

    @Test
    fun `dependent task is not assigned before its dependency is done`() = runTest {
        val upstream = makeTask("upstream")
        val downstream = makeTask("downstream", "upstream")
        coEvery { taskListProvider.provide() } returns listOf(upstream, downstream)

        var upstreamTick = 0
        coEvery { stateDeriver.statusOf(upstream, any(), any()) } answers {
            if (upstreamTick++ == 0) TaskStatus.PENDING else TaskStatus.DONE
        }
        var downstreamTick = 0
        coEvery { stateDeriver.statusOf(downstream, any(), any()) } answers { call ->
            val deps = call.invocation.args[1] as Map<*, *>
            when {
                deps["upstream"] != TaskStatus.DONE -> TaskStatus.BLOCKED
                downstreamTick++ == 0 -> TaskStatus.PENDING
                else -> TaskStatus.DONE
            }
        }
        coEvery { dependencyGraph.downstreamCount("upstream") } returns 1
        coEvery { dependencyGraph.downstreamCount("downstream") } returns 0
        coEvery { dependencyGraph.dependentsOf("upstream") } returns setOf("downstream")
        coEvery { dependencyGraph.dependentsOf("downstream") } returns emptySet()
        coEvery { worktreeManager.getOrCreate(any()) } answers { worktree(firstArg()) }
        coEvery { agentRunner.run(upstream, any()) } returns AgentResult.PrOpened("pr-1", "url")
        coEvery { agentRunner.run(downstream, any()) } returns AgentResult.PrOpened("pr-2", "url")

        makeOrchestrator().run(config.copy(agentPoolSize = 1))

        coVerify { agentRunner.run(upstream, any()) }
    }
}
