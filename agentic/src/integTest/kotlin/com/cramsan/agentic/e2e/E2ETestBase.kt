package com.cramsan.agentic.e2e

import com.cramsan.agentic.core.Task
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

abstract class E2ETestBase {

    companion object {
        @JvmStatic
        protected lateinit var anthropicApiKey: String

        @JvmStatic
        protected lateinit var githubToken: String

        @TempDir
        @JvmStatic
        lateinit var testRepoRoot: Path

        @BeforeAll
        @JvmStatic
        fun checkEnvironment() {
            anthropicApiKey = System.getenv("ANTHROPIC_API_KEY") ?: ""
            githubToken = System.getenv("GITHUB_TOKEN") ?: ""

            assumeTrue(
                anthropicApiKey.isNotBlank(),
                "ANTHROPIC_API_KEY not set, skipping E2E tests"
            )
            assumeTrue(
                githubToken.isNotBlank(),
                "GITHUB_TOKEN not set, skipping E2E tests"
            )
        }
    }

    protected fun initGitRepo(dir: Path) {
        val shell = com.cramsan.agentic.vcs.github.ShellRunner()
        kotlinx.coroutines.runBlocking {
            shell.run("git", "init", dir.toString())
            shell.run("git", "-C", dir.toString(), "config", "user.email", "e2e-test@agentic.dev")
            shell.run("git", "-C", dir.toString(), "config", "user.name", "E2E Test Agent")
            shell.run("git", "-C", dir.toString(), "commit", "--allow-empty", "-m", "E2E test init commit")
        }
    }

    protected fun writeTaskList(dir: Path, tasks: List<Task>) {
        val content = tasks.joinToString("\n\n") { task ->
            """## Task: ${task.id}
Title: ${task.title}
Description: ${task.description}
Dependencies: ${task.dependencies.joinToString(", ").ifEmpty { "none" }}
Timeout: ${task.timeoutSeconds}"""
        }
        Files.createDirectories(dir)
        Files.writeString(dir.resolve("task-list.md"), content)
    }
}
