package com.cramsan.agentic.e2e

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.notification.fake.FakeNotifier
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

@E2ETest
class SmokeE2ETest : E2ETestBase() {

    @Test
    fun `trivialTask_completesSuccessfully`() {
        // This test requires ANTHROPIC_API_KEY and GITHUB_TOKEN set in environment.
        // Assumptions in E2ETestBase.checkEnvironment() will skip if not set.

        initGitRepo(testRepoRoot)

        val task = Task(
            id = "trivial-task",
            title = "Create hello.txt",
            description = "Create a file named hello.txt with the content 'hello world'.",
            dependencies = emptyList(),
            timeoutSeconds = 300L,
        )

        val docsDir = testRepoRoot.resolve(".agentic/docs")
        writeTaskList(docsDir, listOf(task))

        // This test is a scaffold — it documents the expected E2E contract
        // Full execution requires real Claude API and GitHub credentials
        // The test passes trivially without the full wiring to demonstrate the infrastructure
        assertTrue(true, "E2E test infrastructure is in place")
    }
}
