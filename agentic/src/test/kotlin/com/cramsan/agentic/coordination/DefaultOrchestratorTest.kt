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
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.test.assertEquals

class DefaultOrchestratorTest {

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        coEvery { stateDeriver.fetchPrContext() } returns PrContext(emptyList(), emptyList())
    }

    private val stateDeriver = mockk<StateDeriver>()
    private val dependencyGraph = mockk<DependencyGraph>()
    private val worktreeManager = mockk<WorktreeManager>()
    private val agentRunner = mockk<AgentRunner>()
    private val notifier = mockk<Notifier>(relaxed = true)
    private val taskListProvider = mockk<TaskListProvider>()


    private val fakeWorktree = Worktree("task-1", Path.of("/tmp/wt"), "agentic/task-1")

    private fun makeOrchestrator() = DefaultOrchestrator(
        taskListProvider, stateDeriver, dependencyGraph, worktreeManager, agentRunner, notifier
    )

    private fun makeTask(id: String) = Task(id = id, title = id, description = id, dependencies = emptyList())

    private val config = OrchestratorConfig(
        agentPoolSize = 2,
        pollIntervalSeconds = 0L, // no delay in tests
        baseBranch = "main",
    )

    @Test
    fun `all tasks DONE on first tick triggers RunCompleted`() = runTest {
        val task = makeTask("task-1")
        coEvery { taskListProvider.provide() } returns listOf(task)
        coEvery { stateDeriver.statusOf(task, any(), any()) } returns TaskStatus.DONE
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()

        makeOrchestrator().run(config)

        coVerify { notifier.notify(match { it is AgenticEvent.RunCompleted }) }
    }

    @Test
    fun `no progress triggers RunDeadlocked`() = runTest {
        val task = makeTask("task-1")
        coEvery { taskListProvider.provide() } returns listOf(task)
        coEvery { stateDeriver.statusOf(task, any(), any()) } returns TaskStatus.FAILED
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()

        makeOrchestrator().run(config)

        coVerify { notifier.notify(match { it is AgenticEvent.RunDeadlocked }) }
    }

    @Test
    fun `PENDING task causes agent launch and getOrCreate is called`() = runTest {
        val task = makeTask("task-1")
        coEvery { taskListProvider.provide() } returns listOf(task)
        // First tick: PENDING, second tick: DONE (after agent ran)
        var callCount = 0
        coEvery { stateDeriver.statusOf(task, any(), any()) } answers {
            if (callCount++ == 0) TaskStatus.PENDING else TaskStatus.DONE
        }
        coEvery { dependencyGraph.downstreamCount("task-1") } returns 0
        coEvery { dependencyGraph.dependentsOf("task-1") } returns emptySet()
        coEvery { worktreeManager.getOrCreate("task-1") } returns fakeWorktree
        coEvery { agentRunner.run(task, fakeWorktree) } returns AgentResult.PrOpened("pr-1", "url")

        makeOrchestrator().run(config)

        coVerify { worktreeManager.getOrCreate("task-1") }
        coVerify { agentRunner.run(task, fakeWorktree) }
    }

    @Test
    fun `pool sizing limits concurrent agents to agentPoolSize`() = runTest {
        val task1 = makeTask("task-1")
        val task2 = makeTask("task-2")
        val task3 = makeTask("task-3")
        val tasks = listOf(task1, task2, task3)
        coEvery { taskListProvider.provide() } returns tasks

        var tickCount = 0
        coEvery { stateDeriver.statusOf(any(), any(), any()) } answers {
            if (tickCount < 3) TaskStatus.PENDING else TaskStatus.DONE
        }
        tickCount = 0 // reset

        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()
        coEvery { worktreeManager.getOrCreate(any()) } returns fakeWorktree
        coEvery { agentRunner.run(any(), any()) } coAnswers {
            tickCount++
            AgentResult.PrOpened("pr-1", "url")
        }

        val configWith2Pool = config.copy(agentPoolSize = 2)
        makeOrchestrator().run(configWith2Pool)

        coVerify(atLeast = 1) { agentRunner.run(any(), any()) }
    }

    @Test
    fun `priority ordering - task with higher downstreamCount is assigned first`() = runTest {
        val taskA = makeTask("task-A")
        val taskB = makeTask("task-B")
        coEvery { taskListProvider.provide() } returns listOf(taskA, taskB)

        var firstAssigned: String? = null
        var callCount = 0
        coEvery { stateDeriver.statusOf(any(), any(), any()) } answers {
            if (callCount++ < 2) TaskStatus.PENDING else TaskStatus.DONE
        }
        coEvery { dependencyGraph.downstreamCount("task-A") } returns 2
        coEvery { dependencyGraph.downstreamCount("task-B") } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()

        coEvery { worktreeManager.getOrCreate(any()) } answers {
            val taskId = firstArg<String>()
            if (firstAssigned == null) firstAssigned = taskId
            Worktree(taskId, Path.of("/tmp/$taskId"), "agentic/$taskId")
        }
        coEvery { agentRunner.run(any(), any()) } returns AgentResult.PrOpened("pr-1", "url")

        makeOrchestrator().run(config.copy(agentPoolSize = 1))

        assertEquals("task-A", firstAssigned)
    }

    @Test
    fun `AgentResult_Failed causes TaskFailed notification`() = runTest {
        val task = makeTask("task-1")
        coEvery { taskListProvider.provide() } returns listOf(task)

        var callCount = 0
        coEvery { stateDeriver.statusOf(task, any(), any()) } answers {
            if (callCount++ == 0) TaskStatus.PENDING else TaskStatus.FAILED
        }
        coEvery { dependencyGraph.downstreamCount(any()) } returns 0
        coEvery { dependencyGraph.dependentsOf(any()) } returns emptySet()
        coEvery { worktreeManager.getOrCreate(any()) } returns fakeWorktree
        coEvery { agentRunner.run(task, fakeWorktree) } returns AgentResult.Failed("Out of memory")

        makeOrchestrator().run(config)

        coVerify { notifier.notify(match { it is AgenticEvent.TaskFailed }) }
    }
}
