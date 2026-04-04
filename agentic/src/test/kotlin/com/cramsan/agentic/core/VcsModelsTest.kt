package com.cramsan.agentic.core

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class VcsModelsTest {

    private val json = Json { prettyPrint = false }

    @Test
    fun `PullRequest round-trips through JSON`() {
        val original = PullRequest(
            id = "pr-42",
            url = "https://github.com/cramsan/MonoRepo/pull/42",
            title = "Add agentic module",
            state = PullRequestState.OPEN,
            sourceBranch = "feature/agentic",
            targetBranch = "main",
            labels = listOf("enhancement", "agentic"),
        )

        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<PullRequest>(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun `PullRequest with empty labels round-trips through JSON`() {
        val original = PullRequest(
            id = "pr-1",
            url = "https://github.com/owner/repo/pull/1",
            title = "Initial commit",
            state = PullRequestState.MERGED,
            sourceBranch = "dev",
            targetBranch = "main",
            labels = emptyList(),
        )

        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<PullRequest>(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun `PullRequestState values round-trip through JSON`() {
        for (state in PullRequestState.entries) {
            val encoded = json.encodeToString(state)
            val decoded = json.decodeFromString<PullRequestState>(encoded)
            assertEquals(state, decoded)
        }
    }

    @Test
    fun `PullRequestComment round-trips through JSON`() {
        val original = PullRequestComment(
            author = "reviewer",
            body = "LGTM!",
            createdAtEpochMs = 1700000000000L,
        )

        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<PullRequestComment>(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun `PullRequest CLOSED state round-trips through JSON`() {
        val original = PullRequest(
            id = "pr-99",
            url = "https://github.com/owner/repo/pull/99",
            title = "Closed PR",
            state = PullRequestState.CLOSED,
            sourceBranch = "fix/bug",
            targetBranch = "main",
            labels = listOf("bug"),
        )

        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<PullRequest>(encoded)

        assertEquals(original, decoded)
    }
}
