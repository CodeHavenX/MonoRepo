package com.cramsan.agentic.vcs.local

import com.cramsan.agentic.vcs.github.ShellResult
import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.agentic.vcs.github.VcsProviderException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.assertFailsWith

class LocalVcsProviderNegativeTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var stateFile: Path
    private val shell = mockk<ShellRunner>()
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeEach
    fun setup() {
        stateFile = tempDir.resolve("local_prs.json")
    }

    @Test
    fun `isPullRequestMerged throws for unknown prId`() = runTest {
        val provider = manualMergeProvider()

        assertFailsWith<IllegalArgumentException> {
            provider.isPullRequestMerged("999")
        }
    }

    @Test
    fun `getPullRequestComments throws for unknown prId`() = runTest {
        val provider = manualMergeProvider()

        assertFailsWith<IllegalArgumentException> {
            provider.getPullRequestComments("999")
        }
    }

    @Test
    fun `addPullRequestComment throws for unknown prId`() = runTest {
        val provider = manualMergeProvider()

        assertFailsWith<IllegalArgumentException> {
            provider.addPullRequestComment("999", "Some comment")
        }
    }

    @Test
    fun `pullRequestHasRequestedChanges throws for unknown prId`() = runTest {
        val provider = manualMergeProvider()

        assertFailsWith<IllegalArgumentException> {
            provider.pullRequestHasRequestedChanges("999")
        }
    }

    @Test
    fun `autoMerge=true git merge failure throws VcsProviderException`() = runTest {
        val provider = autoMergeProvider()
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult(
            "",
            1,
            "CONFLICT (content): Merge conflict in src/Foo.kt",
        )

        assertFailsWith<VcsProviderException> {
            provider.createPullRequest("feature/a", "main", "PR 1", "Body")
        }
    }

    @Test
    fun `createPullRequest throws when an open PR already exists for the same branch`() = runTest {
        val provider = manualMergeProvider()
        provider.createPullRequest("feature/a", "main", "PR 1", "Body")

        assertFailsWith<IllegalStateException> {
            provider.createPullRequest("feature/a", "main", "PR 1 duplicate", "Body")
        }
    }

    @Test
    fun `isPullRequestMerged with autoMerge=false throws VcsProviderException on git failure`() = runTest {
        val provider = manualMergeProvider()
        provider.createPullRequest("feature/a", "main", "PR 1", "Body")
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult(
            "",
            128,
            "fatal: not a git repository",
        )

        val pr = provider.listOpenPullRequests().first()
        assertFailsWith<VcsProviderException> {
            provider.isPullRequestMerged(pr.id)
        }
    }

    @Test
    fun `creating a second PR on the same branch is allowed after the first is no longer OPEN`() = runTest {
        val provider = autoMergeProvider()
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        // First PR is auto-merged (MERGED state), so a second one should be allowed
        provider.createPullRequest("feature/a", "main", "PR 1", "Body")
        provider.createPullRequest("feature/a", "main", "PR 2", "Body")

        val merged = provider.listMergedPullRequests()
        val open = provider.listOpenPullRequests()
        // Both end up merged; neither is OPEN so no duplicate-PR conflict
        kotlin.test.assertEquals(2, merged.size)
        kotlin.test.assertEquals(0, open.size)
    }

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

    // endregion
}
