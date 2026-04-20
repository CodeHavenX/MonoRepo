package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus
import com.cramsan.agentic.coordination.fake.FakeTaskListProvider
import com.cramsan.agentic.execution.AgentResult
import com.cramsan.agentic.execution.AgentRunner
import com.cramsan.agentic.execution.DefaultWorktreeManager
import com.cramsan.agentic.execution.Worktree
import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.notification.fake.FakeNotifier
import com.cramsan.agentic.vcs.fake.FakeVcsProvider
import com.cramsan.agentic.vcs.github.ShellResult
import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertTrue

class OrchestratorLifecycleIntegrationTest {

    @TempDir
    lateinit var repoRoot: Path

    @TempDir
    lateinit var agenticDir: Path

    private lateinit var fakeVcsProvider: FakeVcsProvider
    private lateinit var fakeNotifier: FakeNotifier
    private lateinit var shell: ShellRunner
    private lateinit var worktreeManager: DefaultWorktreeManager

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        fakeVcsProvider = FakeVcsProvider()
        fakeNotifier = FakeNotifier()
        shell = mockk()
        // Mock shell to succeed for all git commands
        coEvery { shell.run(*anyVararg()) } returns ShellResult("", 0, "")
        worktreeManager = DefaultWorktreeManager(repoRoot, agenticDir, "main", shell)
    }

    private fun makeTask(id: String, vararg deps: String) = Task(
        id = id, title = "Task $id", description = "Description for $id",
        dependencies = deps.toList(), timeoutSeconds = 60L,
    )

    private fun writeTaskListFile(tasks: List<Task>): Path {
        val content = tasks.joinToString("\n\n") { task ->
            """## Task: ${task.id}
Title: ${task.title}
Description: ${task.description}
Dependencies: ${task.dependencies.joinToString(", ").ifEmpty { "none" }}
Timeout: ${task.timeoutSeconds}"""
        }
        val path = agenticDir.resolve("task-list.md")
        Files.writeString(path, content)
        return path
    }

    @Test
    fun `full lifecycle - linear dependency - RunCompleted fires`() = runTest {
        val taskA = makeTask("task-a")
        val taskB = makeTask("task-b", "task-a")
        val tasks = listOf(taskA, taskB)

        val taskListProvider = FakeTaskListProvider(tasks)
        val dependencyGraph = DefaultDependencyGraph(tasks)
        val stateDeriver = DefaultStateDeriver(fakeVcsProvider, worktreeManager, agenticDir)

        val agentRunner = mockk<AgentRunner>()
        coEvery { agentRunner.run(any(), any()) } coAnswers {
            val task = firstArg<Task>()
            val worktree = secondArg<Worktree>()
            // Simulate agent opening a PR then the PR being merged
            val pr = fakeVcsProvider.createPullRequest(
                sourceBranch = "agentic/${task.id}",
                targetBranch = "main",
                title = "Task ${task.id}",
                body = "",
                labels = listOf("agentic-code"),
            )
            fakeVcsProvider.mergePullRequest(pr.id)
            AgentResult.PrOpened(pr.id, pr.url)
        }

        val orchestrator = DefaultOrchestrator(
            taskListProvider, stateDeriver, dependencyGraph, worktreeManager, agentRunner, fakeNotifier
        )

        val config = OrchestratorConfig(
            agentPoolSize = 2,
            pollIntervalSeconds = 0L,
            baseBranch = "main",
        )

        orchestrator.run(config)

        assertTrue(
            fakeNotifier.receivedEvents.any { it is AgenticEvent.RunCompleted },
            "Expected RunCompleted event but got: ${fakeNotifier.receivedEvents}"
        )
    }

    @Test
    fun `deadlock - failed task causes dependent to be blocked - RunDeadlocked fires`() = runTest {
        val taskA = makeTask("task-a")
        val taskB = makeTask("task-b", "task-a")
        val tasks = listOf(taskA, taskB)

        val taskListProvider = FakeTaskListProvider(tasks)
        val dependencyGraph = DefaultDependencyGraph(tasks)
        val stateDeriver = DefaultStateDeriver(fakeVcsProvider, worktreeManager, agenticDir)

        val agentRunner = mockk<AgentRunner>()
        coEvery { agentRunner.run(taskA, any()) } coAnswers {
            val failedFile = agenticDir.resolve("tasks/task-a/failed.txt")
            java.nio.file.Files.createDirectories(failedFile.parent)
            java.nio.file.Files.writeString(failedFile, "Cannot proceed")
            AgentResult.Failed("Cannot proceed")
        }
        // task-B should never run since task-A is blocked

        val orchestrator = DefaultOrchestrator(
            taskListProvider, stateDeriver, dependencyGraph, worktreeManager, agentRunner, fakeNotifier
        )

        val config = OrchestratorConfig(
            agentPoolSize = 2,
            pollIntervalSeconds = 0L,
            baseBranch = "main",
        )

        orchestrator.run(config)

        assertTrue(
            fakeNotifier.receivedEvents.any { it is AgenticEvent.RunDeadlocked },
            "Expected RunDeadlocked event but got: ${fakeNotifier.receivedEvents}"
        )
    }
}
