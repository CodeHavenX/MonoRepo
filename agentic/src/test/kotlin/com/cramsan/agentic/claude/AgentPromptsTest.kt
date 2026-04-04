package com.cramsan.agentic.claude

import com.cramsan.agentic.core.PullRequestComment
import com.cramsan.agentic.core.Task
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

/**
 * Tests for AgentPrompts enforcing that each prompt contains all elements required
 * by TECH_DESIGN.md §7.3 (context reset strategy) and ARCHITECTURE.md §3.5.
 */
class AgentPromptsTest {

    private val task = Task(
        id = "task-abc",
        title = "Implement auth module",
        description = "Add JWT-based authentication following the standards document.",
        dependencies = emptyList(),
    )

    // ── buildTaskStartPrompt ──────────────────────────────────────────────────

    @Test
    fun `buildTaskStartPrompt includes task id`() {
        val prompt = buildTaskStartPrompt(task, emptyList())
        assertTrue(prompt.contains("task-abc"), "Prompt must include task id. Got: $prompt")
    }

    @Test
    fun `buildTaskStartPrompt includes task title`() {
        val prompt = buildTaskStartPrompt(task, emptyList())
        assertTrue(prompt.contains("Implement auth module"), "Prompt must include task title. Got: $prompt")
    }

    @Test
    fun `buildTaskStartPrompt includes task description`() {
        val prompt = buildTaskStartPrompt(task, emptyList())
        assertTrue(prompt.contains("JWT-based authentication"), "Prompt must include task description. Got: $prompt")
    }

    @Test
    fun `buildTaskStartPrompt includes provided document content`() {
        val docs = listOf(
            "standards.md" to "# Standards\nAll code must have unit tests.",
            "architecture.md" to "# Architecture\nUse hexagonal architecture.",
        )
        val prompt = buildTaskStartPrompt(task, docs)
        assertTrue(prompt.contains("All code must have unit tests"), "Prompt must include document content. Got: $prompt")
        assertTrue(prompt.contains("hexagonal architecture"), "Prompt must include all documents. Got: $prompt")
    }

    @Test
    fun `buildTaskStartPrompt instructs agent to commit and push before task_complete`() {
        val prompt = buildTaskStartPrompt(task, emptyList())
        // Spec requires explicit git commit+push instructions before calling task_complete
        val hasGitInstructions = prompt.contains("git add") || prompt.contains("git commit") || prompt.contains("git push")
        assertTrue(hasGitInstructions, "Prompt must include git commit/push instructions. Got: $prompt")
    }

    @Test
    fun `buildTaskStartPrompt instructs agent to use task_complete tool`() {
        val prompt = buildTaskStartPrompt(task, emptyList())
        assertTrue(prompt.contains("task_complete"), "Prompt must reference the task_complete tool. Got: $prompt")
    }

    @Test
    fun `buildTaskStartPrompt instructs agent to use task_failed on blocker`() {
        val prompt = buildTaskStartPrompt(task, emptyList())
        assertTrue(prompt.contains("task_failed"), "Prompt must reference task_failed for unresolvable blockers. Got: $prompt")
    }

    @Test
    fun `buildTaskStartPrompt mentions propose_amendment for document changes`() {
        val prompt = buildTaskStartPrompt(task, emptyList())
        assertTrue(prompt.contains("propose_amendment"), "Prompt must mention propose_amendment. Got: $prompt")
    }

    @Test
    fun `buildTaskStartPrompt mentions split_task for oversized tasks`() {
        val prompt = buildTaskStartPrompt(task, emptyList())
        assertTrue(prompt.contains("split_task"), "Prompt must mention split_task for large tasks. Got: $prompt")
    }

    // ── buildPrOpenedPrompt ───────────────────────────────────────────────────

    @Test
    fun `buildPrOpenedPrompt includes task id`() {
        val prompt = buildPrOpenedPrompt(task, "+ added line", "PR description")
        assertTrue(prompt.contains("task-abc"), "PR opened prompt must include task id. Got: $prompt")
    }

    @Test
    fun `buildPrOpenedPrompt includes git diff content`() {
        val diff = "+ fun newFunction() = Unit"
        val prompt = buildPrOpenedPrompt(task, diff, "my PR")
        assertTrue(prompt.contains("newFunction"), "PR opened prompt must include git diff. Got: $prompt")
    }

    // ── buildChangesRequestedPrompt ───────────────────────────────────────────

    @Test
    fun `buildChangesRequestedPrompt includes task id`() {
        val prompt = buildChangesRequestedPrompt(task, "- old", emptyList())
        assertTrue(prompt.contains("task-abc"), "Changes-requested prompt must include task id. Got: $prompt")
    }

    @Test
    fun `buildChangesRequestedPrompt includes git diff`() {
        val diff = "- removed line\n+ added line"
        val prompt = buildChangesRequestedPrompt(task, diff, emptyList())
        assertTrue(prompt.contains("removed line"), "Changes-requested prompt must include git diff. Got: $prompt")
    }

    @Test
    fun `buildChangesRequestedPrompt includes reviewer comments`() {
        val comments = listOf(
            PullRequestComment(author = "reviewer1", body = "Please add tests for edge cases.", createdAtEpochMs = 0L),
            PullRequestComment(author = "reviewer2", body = "Variable name is misleading.", createdAtEpochMs = 0L),
        )
        val prompt = buildChangesRequestedPrompt(task, "", comments)
        assertTrue(prompt.contains("Please add tests for edge cases."), "Prompt must include reviewer comment body. Got: $prompt")
        assertTrue(prompt.contains("Variable name is misleading."), "Prompt must include all reviewer comments. Got: $prompt")
    }

    @Test
    fun `buildChangesRequestedPrompt includes reviewer author names`() {
        val comments = listOf(
            PullRequestComment(author = "alice", body = "Fix this", createdAtEpochMs = 0L),
        )
        val prompt = buildChangesRequestedPrompt(task, "", comments)
        assertTrue(prompt.contains("alice"), "Prompt must include reviewer author names. Got: $prompt")
    }

    @Test
    fun `buildChangesRequestedPrompt instructs agent to commit push and call task_complete`() {
        val prompt = buildChangesRequestedPrompt(task, "", emptyList())
        val hasGitInstructions = prompt.contains("git commit") || prompt.contains("git push") || prompt.contains("git add")
        assertTrue(hasGitInstructions, "Changes-requested prompt must include git commit/push instructions. Got: $prompt")
        assertTrue(prompt.contains("task_complete"), "Changes-requested prompt must reference task_complete. Got: $prompt")
    }

    // ── buildResumeFromWorktreePrompt ─────────────────────────────────────────

    @Test
    fun `buildResumeFromWorktreePrompt includes task id`() {
        val prompt = buildResumeFromWorktreePrompt(task, "+ partial change")
        assertTrue(prompt.contains("task-abc"), "Resume prompt must include task id. Got: $prompt")
    }

    @Test
    fun `buildResumeFromWorktreePrompt includes existing git diff`() {
        val diff = "+ half-implemented function body"
        val prompt = buildResumeFromWorktreePrompt(task, diff)
        assertTrue(prompt.contains("half-implemented function body"), "Resume prompt must include current git diff. Got: $prompt")
    }

    @Test
    fun `buildResumeFromWorktreePrompt signals the agent session was interrupted`() {
        val prompt = buildResumeFromWorktreePrompt(task, "")
        val hasResumeCue = prompt.contains("interrupted") || prompt.contains("resume") || prompt.contains("Resume") ||
            prompt.contains("previous") || prompt.contains("pick up") || prompt.contains("continuing")
        assertTrue(hasResumeCue, "Resume prompt must convey that the session was interrupted. Got: $prompt")
    }

    @Test
    fun `buildResumeFromWorktreePrompt instructs agent to use task_complete when done`() {
        val prompt = buildResumeFromWorktreePrompt(task, "")
        assertTrue(prompt.contains("task_complete"), "Resume prompt must reference task_complete. Got: $prompt")
    }
}
