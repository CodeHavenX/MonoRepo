package com.cramsan.agentic.vcs.local

import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.vcs.github.ShellRunner
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

class LocalVcsProviderTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var stateFile: Path
    private lateinit var provider: LocalVcsProvider

    // Shell is never called in these tests (autoMerge=false, no isPullRequestMerged git check)
    private val shell = mockk<ShellRunner>()
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeEach
    fun setup() {
        stateFile = tempDir.resolve("local_prs.json")
        provider = LocalVcsProvider(
            stateFile = stateFile,
            autoMerge = false,
            repoRoot = tempDir,
            shell = shell,
            json = json,
        )
    }

    @Test
    fun `createPullRequest returns PR with state OPEN when autoMerge is false`() = runTest {
        val pr = provider.createPullRequest("feature/a", "main", "PR 1", "Body")

        assertEquals(PullRequestState.OPEN, pr.state)
        assertEquals("feature/a", pr.sourceBranch)
        assertEquals("main", pr.targetBranch)
        assertEquals("PR 1", pr.title)
    }

    @Test
    fun `createPullRequest assigns sequential IDs`() = runTest {
        val pr1 = provider.createPullRequest("feature/a", "main", "PR 1", "Body 1")
        val pr2 = provider.createPullRequest("feature/b", "main", "PR 2", "Body 2")

        assertTrue(pr1.id != pr2.id)
    }

    @Test
    fun `createPullRequest sets labels on returned PR`() = runTest {
        val pr = provider.createPullRequest("feature/a", "main", "PR 1", "Body", listOf("agentic-code"))

        assertEquals(listOf("agentic-code"), pr.labels)
    }

    @Test
    fun `listOpenPullRequests returns all OPEN PRs`() = runTest {
        provider.createPullRequest("feature/a", "main", "PR 1", "Body")
        provider.createPullRequest("feature/b", "main", "PR 2", "Body")

        val open = provider.listOpenPullRequests()

        assertEquals(2, open.size)
        assertTrue(open.all { it.state == PullRequestState.OPEN })
    }

    @Test
    fun `listMergedPullRequests returns empty when no PRs are merged`() = runTest {
        provider.createPullRequest("feature/a", "main", "PR 1", "Body")

        val merged = provider.listMergedPullRequests()

        assertEquals(0, merged.size)
    }

    @Test
    fun `listOpenPullRequests filters by labels`() = runTest {
        provider.createPullRequest("feature/a", "main", "PR 1", "Body", listOf("agentic-code"))
        provider.createPullRequest("feature/b", "main", "PR 2", "Body", listOf("agentic-document"))

        val codeOnly = provider.listOpenPullRequests(listOf("agentic-code"))

        assertEquals(1, codeOnly.size)
        assertEquals("feature/a", codeOnly[0].sourceBranch)
    }

    @Test
    fun `listOpenPullRequests with empty labels returns all OPEN PRs`() = runTest {
        provider.createPullRequest("feature/a", "main", "PR 1", "Body", listOf("agentic-code"))
        provider.createPullRequest("feature/b", "main", "PR 2", "Body", listOf("agentic-document"))

        val all = provider.listOpenPullRequests()

        assertEquals(2, all.size)
    }

    @Test
    fun `listMergedPullRequests filters by labels`() = runTest {
        provider.createPullRequest("feature/a", "main", "PR 1", "Body", listOf("agentic-code"))
        provider.createPullRequest("feature/b", "main", "PR 2", "Body", listOf("agentic-document"))
        // Directly manipulate the state file to mark one PR as MERGED
        val rawState = json.decodeFromString(LocalPrState.serializer(), stateFile.toFile().readText())
        rawState.prs[0].state = PullRequestState.MERGED
        stateFile.toFile().writeText(json.encodeToString(LocalPrState.serializer(), rawState))

        val merged = provider.listMergedPullRequests(listOf("agentic-code"))

        assertEquals(1, merged.size)
        assertEquals("feature/a", merged[0].sourceBranch)
    }

    @Test
    fun `addPullRequestComment is reflected in getPullRequestComments`() = runTest {
        val pr = provider.createPullRequest("feature/a", "main", "PR 1", "Body")
        provider.addPullRequestComment(pr.id, "LGTM!")

        val comments = provider.getPullRequestComments(pr.id)

        assertEquals(1, comments.size)
        assertEquals("LGTM!", comments[0].body)
        assertEquals("agentic-bot", comments[0].author)
    }

    @Test
    fun `multiple comments accumulate on the same PR`() = runTest {
        val pr = provider.createPullRequest("feature/a", "main", "PR 1", "Body")
        provider.addPullRequestComment(pr.id, "First comment")
        provider.addPullRequestComment(pr.id, "Second comment")

        val comments = provider.getPullRequestComments(pr.id)

        assertEquals(2, comments.size)
        assertEquals("First comment", comments[0].body)
        assertEquals("Second comment", comments[1].body)
    }

    @Test
    fun `pullRequestHasRequestedChanges returns false by default`() = runTest {
        val pr = provider.createPullRequest("feature/a", "main", "PR 1", "Body")

        assertFalse(provider.pullRequestHasRequestedChanges(pr.id))
    }

    @Test
    fun `state file is created when it does not exist`() = runTest {
        assertFalse(stateFile.toFile().exists())

        provider.createPullRequest("feature/a", "main", "PR 1", "Body")

        assertTrue(stateFile.toFile().exists())
    }

    @Test
    fun `state file is created inside a nested directory`() = runTest {
        val nestedStateFile = tempDir.resolve("nested/dir/local_prs.json")
        val nestedProvider = LocalVcsProvider(
            stateFile = nestedStateFile,
            autoMerge = false,
            repoRoot = tempDir,
            shell = shell,
            json = json,
        )

        nestedProvider.createPullRequest("feature/a", "main", "PR 1", "Body")

        assertTrue(nestedStateFile.toFile().exists())
    }
}
