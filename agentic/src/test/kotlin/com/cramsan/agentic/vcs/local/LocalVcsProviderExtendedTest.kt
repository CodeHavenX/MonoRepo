package com.cramsan.agentic.vcs.local

import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.vcs.github.ShellResult
import com.cramsan.agentic.vcs.github.ShellRunner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LocalVcsProviderExtendedTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var stateFile: Path
    private val shell = mockk<ShellRunner>()
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeEach
    fun setup() {
        stateFile = tempDir.resolve("local_prs.json")
    }

    // region autoMerge=true

    @Test
    fun `autoMerge=true causes createPullRequest to set state MERGED`() = runTest {
        val provider = autoMergeProvider()
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        val pr = provider.createPullRequest("feature/a", "main", "PR 1", "Body")

        assertEquals(PullRequestState.MERGED, pr.state)
    }

    @Test
    fun `autoMerge=true createPullRequest runs git merge with correct arguments`() = runTest {
        val provider = autoMergeProvider()
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        provider.createPullRequest("feature/a", "main", "PR 1", "Body")

        coVerify {
            shell.run(
                "git", "merge", "--no-ff", "feature/a",
                workingDir = repoRoot(),
            )
        }
    }

    @Test
    fun `autoMerge=true merged PR appears in listMergedPullRequests`() = runTest {
        val provider = autoMergeProvider()
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        provider.createPullRequest("feature/a", "main", "PR 1", "Body", listOf("agentic-code"))

        val merged = provider.listMergedPullRequests(listOf("agentic-code"))
        assertEquals(1, merged.size)
        assertEquals("feature/a", merged[0].sourceBranch)
    }

    @Test
    fun `autoMerge=true merged PR does not appear in listOpenPullRequests`() = runTest {
        val provider = autoMergeProvider()
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        provider.createPullRequest("feature/a", "main", "PR 1", "Body")

        val open = provider.listOpenPullRequests()
        assertEquals(0, open.size)
    }

    @Test
    fun `autoMerge=true isPullRequestMerged returns true without git check`() = runTest {
        val provider = autoMergeProvider()
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        val pr = provider.createPullRequest("feature/a", "main", "PR 1", "Body")

        assertTrue(provider.isPullRequestMerged(pr.id))
        // git branch --merged should NOT be called; only git merge was called during createPullRequest
        coVerify(exactly = 0) {
            shell.run("git", "branch", "--merged", any(), workingDir = any())
        }
    }

    // endregion

    // region autoMerge=false git detection

    @Test
    fun `isPullRequestMerged with autoMerge=false runs git branch --merged`() = runTest {
        val provider = manualMergeProvider()
        provider.createPullRequest("feature/a", "main", "PR 1", "Body")
        // Simulate: feature/a has been merged into main
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult(
            "  main\n  feature/a\n",
            0,
            "",
        )

        val pr = provider.listOpenPullRequests().first()
        assertTrue(provider.isPullRequestMerged(pr.id))

        coVerify {
            shell.run("git", "branch", "--merged", "main", workingDir = repoRoot())
        }
    }

    @Test
    fun `isPullRequestMerged with autoMerge=false returns false when branch is not in merged list`() = runTest {
        val provider = manualMergeProvider()
        provider.createPullRequest("feature/a", "main", "PR 1", "Body")
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult(
            "  main\n  develop\n",
            0,
            "",
        )

        val pr = provider.listOpenPullRequests().first()
        assertFalse(provider.isPullRequestMerged(pr.id))
    }

    @Test
    fun `isPullRequestMerged with autoMerge=false updates state to MERGED when git confirms merge`() = runTest {
        val provider = manualMergeProvider()
        provider.createPullRequest("feature/a", "main", "PR 1", "Body")
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult(
            "  main\n  feature/a\n",
            0,
            "",
        )

        val pr = provider.listOpenPullRequests().first()
        provider.isPullRequestMerged(pr.id)

        // Subsequent call should read MERGED from state without another git check
        coEvery { shell.run(*anyVararg(), workingDir = any()) } throws AssertionError("git should not be called again")
        assertTrue(provider.isPullRequestMerged(pr.id))
    }

    @Test
    fun `isPullRequestMerged handles current branch marker in git branch output`() = runTest {
        val provider = manualMergeProvider()
        provider.createPullRequest("feature/a", "main", "PR 1", "Body")
        // Simulate output where feature/a is the current branch (prefixed with "* ")
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult(
            "* feature/a\n  main\n",
            0,
            "",
        )

        val pr = provider.listOpenPullRequests().first()
        assertTrue(provider.isPullRequestMerged(pr.id))
    }

    // endregion

    // region cross-instance persistence

    @Test
    fun `state persists across separate LocalVcsProvider instances`() = runTest {
        val providerA = manualMergeProvider()
        val pr = providerA.createPullRequest("feature/a", "main", "PR 1", "Body")
        providerA.addPullRequestComment(pr.id, "First comment")

        // New instance reading the same file
        val providerB = manualMergeProvider()
        val prs = providerB.listOpenPullRequests()
        assertEquals(1, prs.size)
        assertEquals("feature/a", prs[0].sourceBranch)

        val comments = providerB.getPullRequestComments(pr.id)
        assertEquals(1, comments.size)
        assertEquals("First comment", comments[0].body)
    }

    @Test
    fun `PR IDs continue incrementing across separate instances`() = runTest {
        val providerA = manualMergeProvider()
        val pr1 = providerA.createPullRequest("feature/a", "main", "PR 1", "Body")

        val providerB = manualMergeProvider()
        val pr2 = providerB.createPullRequest("feature/b", "main", "PR 2", "Body")

        assertTrue(pr1.id != pr2.id)
        assertTrue(pr2.id.toInt() > pr1.id.toInt())
    }

    // endregion

    // region label filtering edge cases

    @Test
    fun `listOpenPullRequests returns PRs matching any of the given labels`() = runTest {
        val provider = manualMergeProvider()
        provider.createPullRequest("feature/a", "main", "PR 1", "Body", listOf("agentic-code"))
        provider.createPullRequest("feature/b", "main", "PR 2", "Body", listOf("agentic-document"))
        provider.createPullRequest("feature/c", "main", "PR 3", "Body", listOf("other"))

        val result = provider.listOpenPullRequests(listOf("agentic-code", "agentic-document"))

        assertEquals(2, result.size)
    }

    @Test
    fun `listMergedPullRequests with empty labels returns all merged PRs`() = runTest {
        val provider = autoMergeProvider()
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        provider.createPullRequest("feature/a", "main", "PR 1", "Body", listOf("agentic-code"))
        provider.createPullRequest("feature/b", "main", "PR 2", "Body", listOf("agentic-document"))

        val all = provider.listMergedPullRequests()
        assertEquals(2, all.size)
    }

    // endregion

    // region helpers

    private fun autoMergeProvider() = LocalVcsProvider(
        stateFile = stateFile,
        autoMerge = true,
        repoRoot = tempDir,
        shell = shell,
        json = json,
    )

    private fun manualMergeProvider() = LocalVcsProvider(
        stateFile = stateFile,
        autoMerge = false,
        repoRoot = tempDir,
        shell = shell,
        json = json,
    )

    private fun repoRoot() = tempDir.toString()

    // endregion
}
