package com.cramsan.agentic.coordination

import com.cramsan.agentic.ai.fake.FakeAiProvider
import com.cramsan.agentic.claude.DefaultAgentSession
import com.cramsan.agentic.coordination.fake.FakeTaskListProvider
import com.cramsan.agentic.core.FakeMode
import com.cramsan.agentic.core.Task
import com.cramsan.agentic.execution.AgentResult
import com.cramsan.agentic.execution.AgentRunner
import com.cramsan.agentic.execution.DefaultAgentRunner
import com.cramsan.agentic.execution.DefaultWorktreeManager
import com.cramsan.agentic.input.DocumentStore
import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.notification.fake.FakeNotifier
import com.cramsan.agentic.reviewer.ReviewerLoader
import com.cramsan.agentic.vcs.fake.FakeVcsProvider
import com.cramsan.agentic.vcs.github.ShellResult
import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Integration tests covering multi-task orchestration scenarios:
 *
 * - Scenario 1: Full-stack linear chain  (A → B → C)
 * - Scenario 2: Diamond fork-join with ordering verification  (A → B,C → D)
 * - Scenario 3: Full-stack complex 8-task branching DAG
 * - Scenario 4: Partial failure propagates to deadlock
 * - Scenario 5: Pool capacity limits concurrency on wide fan-out
 *
 * "Full-stack" tests wire real DefaultAgentSession + FakeAiProvider + FakeVcsProvider.
 * "Orchestrator-level" tests mock AgentRunner to focus on scheduling logic.
 */
class BranchingDependencyIntegrationTest {

    @TempDir lateinit var repoRoot: Path
    @TempDir lateinit var agenticDir: Path

    private lateinit var fakeNotifier: FakeNotifier
    private lateinit var shell: ShellRunner

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        fakeNotifier = FakeNotifier()
        shell = mockk()
        coEvery { shell.run(*anyVararg()) } returns ShellResult("", 0, "")
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun task(id: String, vararg deps: String) = Task(
        id = id,
        title = "Task $id",
        description = "Description for $id",
        dependencies = deps.toList(),
        timeoutSeconds = 60L,
    )

    private val baseConfig = OrchestratorConfig(
        agentPoolSize = 4,
        pollIntervalSeconds = 0L,
        baseBranch = "main",
    )

    /**
     * Builds an orchestrator wired with real DefaultAgentSession and FakeAiProvider.
     * FakeVcsProvider is configured to auto-merge agentic-code PRs so StateDeriver
     * can derive DONE without manual intervention.
     */
    private fun buildFullStackOrchestrator(
        tasks: List<Task>,
        vcs: FakeVcsProvider,
        poolSize: Int = 4,
        dispatcher: CoroutineDispatcher,
    ): DefaultOrchestrator {
        val documentStore = mockk<DocumentStore>()
        val reviewerLoader = mockk<ReviewerLoader>()
        every { documentStore.getAll() } returns emptyList()
        every { reviewerLoader.loadAll() } returns emptyList()

        val worktreeManager = DefaultWorktreeManager(repoRoot, agenticDir, "main", shell)
        val aiProvider = FakeAiProvider(mode = FakeMode.DEMO, autoCompleteAfterTurns = 1)
        val agentSession = DefaultAgentSession(aiProvider, vcs, shell, "main", documentStore)
        val agentRunner = DefaultAgentRunner(agentSession, vcs, emptyList(), reviewerLoader, agenticDir)
        val stateDeriver = DefaultStateDeriver(vcs, worktreeManager, agenticDir)
        val taskListProvider = FakeTaskListProvider(tasks)
        val dependencyGraph = DefaultDependencyGraph(tasks)
        return DefaultOrchestrator(
            taskListProvider, stateDeriver, dependencyGraph, worktreeManager, agentRunner, fakeNotifier, dispatcher,
        )
    }

    /**
     * Builds an orchestrator with a mocked AgentRunner.
     * Caller provides [onRun] which is invoked for each task; typically creates and
     * merges a fake PR so StateDeriver derives DONE on the next poll tick.
     */
    private fun buildMockedOrchestrator(
        tasks: List<Task>,
        vcs: FakeVcsProvider,
        poolSize: Int = 4,
        onRun: suspend (Task) -> AgentResult,
    ): DefaultOrchestrator {
        val worktreeManager = DefaultWorktreeManager(repoRoot, agenticDir, "main", shell)
        val agentRunner = mockk<AgentRunner>()
        coEvery { agentRunner.run(any(), any()) } coAnswers { onRun(firstArg()) }
        val stateDeriver = DefaultStateDeriver(vcs, worktreeManager, agenticDir)
        val taskListProvider = FakeTaskListProvider(tasks)
        val dependencyGraph = DefaultDependencyGraph(tasks)
        return DefaultOrchestrator(
            taskListProvider, stateDeriver, dependencyGraph, worktreeManager, agentRunner, fakeNotifier, Dispatchers.Default,
        )
    }

    // ── Scenario 1: Full-stack linear chain ──────────────────────────────────

    @Test
    fun `fullStack - linear chain A-B-C - all 3 tasks complete`() = runTest(timeout = kotlin.time.Duration.INFINITE) {
        val tasks = listOf(task("A"), task("B", "A"), task("C", "B"))
        val vcs = FakeVcsProvider(autoMergeOnCreate = true)
        val orchestrator = buildFullStackOrchestrator(tasks, vcs, dispatcher = Dispatchers.Default,)
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        orchestrator.run(baseConfig)

        val completedEvent = fakeNotifier.receivedEvents
            .filterIsInstance<AgenticEvent.RunCompleted>().firstOrNull()
        assertTrue(completedEvent != null, "Expected RunCompleted but got: ${fakeNotifier.receivedEvents}")
        assertEquals(3, completedEvent.completedTasks.size)
        assertEquals(setOf("A", "B", "C"), completedEvent.completedTasks.map { it.id }.toSet())
    }

    // ── Scenario 2: Orchestrator diamond fork-join with ordering verification ──

    @Test
    fun `orchestrator - diamond A-(B,C)-D - B and C run before D`() = runTest(timeout = kotlin.time.Duration.INFINITE) {
        /*
         *   A
         *  / \
         * B   C
         *  \ /
         *   D
         *
         * Pool size 1 ensures sequential, deterministic execution so ordering
         * assertions are stable without racing IO threads.
         */
        val tasks = listOf(task("A"), task("B", "A"), task("C", "A"), task("D", "B", "C"))
        val completionOrder = CopyOnWriteArrayList<String>()
        // autoMergeOnCreate so createPullRequest atomically sets MERGED — no open-PR window
        val vcs = FakeVcsProvider(autoMergeOnCreate = true)
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        val orchestrator = buildMockedOrchestrator(tasks, vcs) { t ->
            val pr = vcs.createPullRequest(
                sourceBranch = "agentic/${t.id}", targetBranch = "main",
                title = "Task ${t.id}", body = "", labels = listOf("agentic-code"),
            )
            completionOrder.add(t.id)
            AgentResult.PrOpened(pr.id, pr.url)
        }

        orchestrator.run(baseConfig.copy(agentPoolSize = 1))

        assertTrue(
            fakeNotifier.receivedEvents.any { it is AgenticEvent.RunCompleted },
            "Expected RunCompleted but got: ${fakeNotifier.receivedEvents}",
        )
        assertEquals(setOf("A", "B", "C", "D"), completionOrder.toSet(), "All 4 tasks must run")
        assertTrue(completionOrder.indexOf("A") < completionOrder.indexOf("B"), "A must complete before B")
        assertTrue(completionOrder.indexOf("A") < completionOrder.indexOf("C"), "A must complete before C")
        assertTrue(completionOrder.indexOf("B") < completionOrder.indexOf("D"), "B must complete before D")
        assertTrue(completionOrder.indexOf("C") < completionOrder.indexOf("D"), "C must complete before D")
    }

    // ── Scenario 3: Full-stack complex 8-task branching DAG ──────────────────

    @Test
    fun `fullStack - complex 8-task DAG with cross-branch joins and 3-way fan-in - all tasks complete`() =
        runTest(timeout = kotlin.time.Duration.INFINITE) {
            /*
             * Dependency graph:
             *
             *        A
             *       / \
             *      B   C
             *     / \ / \
             *    D   E   F
             *         \ /
             *          G
             *          |
             *          H
             *
             * E depends on both B and C  (cross-branch join)
             * G depends on D, E, and F   (3-way fan-in)
             */
            val tasks = listOf(
                task("A"),
                task("B", "A"),
                task("C", "A"),
                task("D", "B"),
                task("E", "B", "C"),
                task("F", "C"),
                task("G", "D", "E", "F"),
                task("H", "G"),
            )
            val vcs = FakeVcsProvider(autoMergeOnCreate = true)
            val orchestrator = buildFullStackOrchestrator(tasks, vcs, poolSize = 4, Dispatchers.Default,)
            coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

            orchestrator.run(baseConfig)

            val completedEvent = fakeNotifier.receivedEvents
                .filterIsInstance<AgenticEvent.RunCompleted>().firstOrNull()
            assertTrue(completedEvent != null, "Expected RunCompleted but got: ${fakeNotifier.receivedEvents}")
            assertEquals(8, completedEvent.completedTasks.size)
            assertEquals(
                setOf("A", "B", "C", "D", "E", "F", "G", "H"),
                completedEvent.completedTasks.map { it.id }.toSet(),
            )
        }

    // ── Scenario 4: Partial failure propagates to deadlock ───────────────────

    @Test
    fun `orchestrator - task A fails - dependent B is blocked - RunDeadlocked fires`() = runTest(timeout = kotlin.time.Duration.INFINITE) {
        val tasks = listOf(task("A"), task("B", "A"))
        val vcs = FakeVcsProvider(autoMergeOnCreate = true)
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        val orchestrator = buildMockedOrchestrator(tasks, vcs) { t ->
            if (t.id == "A") {
                val failedFile = agenticDir.resolve("tasks/A/failed.txt")
                Files.createDirectories(failedFile.parent)
                Files.writeString(failedFile, "Simulated failure in task A")
                AgentResult.Failed("Simulated failure in task A")
            } else {
                error("Task ${t.id} should never run — it depends on the failed task A")
            }
        }

        orchestrator.run(baseConfig)

        val deadlockedEvent = fakeNotifier.receivedEvents
            .filterIsInstance<AgenticEvent.RunDeadlocked>().firstOrNull()
        assertTrue(deadlockedEvent != null, "Expected RunDeadlocked but got: ${fakeNotifier.receivedEvents}")
        assertTrue(
            deadlockedEvent.failedTasks.any { it.id == "A" },
            "Expected task A in failedTasks: ${deadlockedEvent.failedTasks.map { it.id }}",
        )
        assertTrue(
            deadlockedEvent.blockedTasks.any { it.id == "B" },
            "Expected task B in blockedTasks: ${deadlockedEvent.blockedTasks.map { it.id }}",
        )
    }

    // ── Scenario 5: Pool capacity limits concurrency on wide fan-out ──────────

    @Test
    fun `orchestrator - wide fan-out with pool size 2 - all 6 tasks eventually complete`() = runTest(timeout = kotlin.time.Duration.INFINITE) {
        /*
         * A has 5 dependents unblocked simultaneously once A completes.
         * Pool size = 2 means at most 2 of them run per tick.
         * All should eventually complete despite the constraint.
         *
         *    A
         *   /|\\ \
         *  B C D E F
         */
        val tasks = listOf(
            task("A"),
            task("B", "A"),
            task("C", "A"),
            task("D", "A"),
            task("E", "A"),
            task("F", "A"),
        )
        val concurrentPeak = AtomicInteger(0)
        val currentConcurrent = AtomicInteger(0)
        val vcs = FakeVcsProvider(autoMergeOnCreate = true)
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        val orchestrator = buildMockedOrchestrator(tasks, vcs, poolSize = 2) { t ->
            val peak = currentConcurrent.incrementAndGet()
            concurrentPeak.getAndUpdate { maxOf(it, peak) }
            val pr = vcs.createPullRequest(
                sourceBranch = "agentic/${t.id}", targetBranch = "main",
                title = "Task ${t.id}", body = "", labels = listOf("agentic-code"),
            )
            currentConcurrent.decrementAndGet()
            AgentResult.PrOpened(pr.id, pr.url)
        }

        orchestrator.run(baseConfig.copy(agentPoolSize = 2))

        val completedEvent = fakeNotifier.receivedEvents
            .filterIsInstance<AgenticEvent.RunCompleted>().firstOrNull()
        assertTrue(completedEvent != null, "Expected RunCompleted but got: ${fakeNotifier.receivedEvents}")
        assertEquals(6, completedEvent.completedTasks.size)
        assertTrue(
            concurrentPeak.get() <= 2,
            "Expected peak concurrency ≤ 2 but was ${concurrentPeak.get()}",
        )
    }
}
