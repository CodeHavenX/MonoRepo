package com.cramsan.agentic.vcs.fake

import com.cramsan.agentic.core.PullRequestState
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FakeVcsProviderTest {

    @Test
    fun `createPullRequest returns PR with state OPEN and increments IDs`() = runTest {
        val provider = FakeVcsProvider()
        val pr1 = provider.createPullRequest("feature/a", "main", "PR 1", "Body 1")
        val pr2 = provider.createPullRequest("feature/b", "main", "PR 2", "Body 2")
        assertEquals(PullRequestState.OPEN, pr1.state)
        assertEquals(PullRequestState.OPEN, pr2.state)
        assertTrue(pr1.id != pr2.id)
    }

    @Test
    fun `mergePullRequest causes isPullRequestMerged to return true`() = runTest {
        val provider = FakeVcsProvider()
        val pr = provider.createPullRequest("feature/a", "main", "PR", "Body")
        assertFalse(provider.isPullRequestMerged(pr.id))
        provider.mergePullRequest(pr.id)
        assertTrue(provider.isPullRequestMerged(pr.id))
    }

    @Test
    fun `requestChanges causes pullRequestHasRequestedChanges to return true`() = runTest {
        val provider = FakeVcsProvider()
        val pr = provider.createPullRequest("feature/a", "main", "PR", "Body")
        assertFalse(provider.pullRequestHasRequestedChanges(pr.id))
        provider.requestChanges(pr.id)
        assertTrue(provider.pullRequestHasRequestedChanges(pr.id))
    }

    @Test
    fun `addPullRequestComment is reflected in getPullRequestComments`() = runTest {
        val provider = FakeVcsProvider()
        val pr = provider.createPullRequest("feature/a", "main", "PR", "Body")
        provider.addPullRequestComment(pr.id, "LGTM!")
        val comments = provider.getPullRequestComments(pr.id)
        assertEquals(1, comments.size)
        assertEquals("LGTM!", comments[0].body)
    }

    @Test
    fun `listOpenPullRequests filters by state`() = runTest {
        val provider = FakeVcsProvider()
        val pr1 = provider.createPullRequest("feature/a", "main", "PR 1", "Body")
        provider.createPullRequest("feature/b", "main", "PR 2", "Body")
        provider.mergePullRequest(pr1.id)
        val open = provider.listOpenPullRequests()
        assertEquals(1, open.size)
        val merged = provider.listMergedPullRequests()
        assertEquals(1, merged.size)
    }
}
