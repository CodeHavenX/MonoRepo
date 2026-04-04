package com.cramsan.agentic.vcs.github

import com.cramsan.agentic.core.PullRequestState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GitHubVcsProviderTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val shell = mockk<ShellRunner>()
    private val provider = GitHubVcsProvider("owner", "repo", shell, json)

    @Test
    fun `createPullRequest assembles correct gh command and maps response`() = runTest {
        val cannedResponse = """
            {
                "number": "42",
                "url": "https://github.com/owner/repo/pull/42",
                "title": "My PR",
                "state": "OPEN",
                "headRefName": "feature/x",
                "baseRefName": "main",
                "labels": []
            }
        """.trimIndent()
        coEvery { shell.run(*anyVararg()) } returns ShellResult(cannedResponse, 0, "")

        val pr = provider.createPullRequest("feature/x", "main", "My PR", "Body")

        assertEquals("42", pr.id)
        assertEquals("My PR", pr.title)
        assertEquals(PullRequestState.OPEN, pr.state)
        assertEquals("feature/x", pr.sourceBranch)

        coVerify {
            shell.run(
                "gh", "pr", "create",
                "--title", "My PR",
                "--body", "Body",
                "--base", "main",
                "--head", "feature/x",
                "--repo", "owner/repo",
                "--json", any(),
            )
        }
    }

    @Test
    fun `getPullRequestComments maps JSON comment array correctly`() = runTest {
        val cannedResponse = """
            {
                "comments": [
                    {"author": {"login": "reviewer1"}, "body": "LGTM!"},
                    {"author": {"login": "reviewer2"}, "body": "Needs work."}
                ]
            }
        """.trimIndent()
        coEvery { shell.run(*anyVararg()) } returns ShellResult(cannedResponse, 0, "")

        val comments = provider.getPullRequestComments("42")
        assertEquals(2, comments.size)
        assertEquals("reviewer1", comments[0].author)
        assertEquals("LGTM!", comments[0].body)
    }

    @Test
    fun `pullRequestHasRequestedChanges returns true when CHANGES_REQUESTED`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult(
            """{"reviewDecision": "CHANGES_REQUESTED"}""", 0, ""
        )
        assertTrue(provider.pullRequestHasRequestedChanges("42"))
    }

    @Test
    fun `pullRequestHasRequestedChanges returns false when APPROVED`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult(
            """{"reviewDecision": "APPROVED"}""", 0, ""
        )
        assertFalse(provider.pullRequestHasRequestedChanges("42"))
    }

    @Test
    fun `createPullRequest throws VcsProviderException on non-zero exit code`() = runTest {
        coEvery { shell.run(*anyVararg()) } returns ShellResult("", 1, "error: repository not found")

        assertFailsWith<VcsProviderException> {
            provider.createPullRequest("feature/x", "main", "My PR", "Body")
        }
    }
}
