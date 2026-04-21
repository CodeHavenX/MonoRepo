package com.cramsan.agentic.notification

/**
 * Delivers [AgenticEvent]s to an operator-visible channel. The orchestrator calls [notify]
 * at most once per termination condition; implementations must be idempotent (posting the
 * same notification twice should not cause duplicate visible output or errors).
 *
 * Implementations:
 * - [com.cramsan.agentic.notification.vcs.VcsCommentNotifier]: posts PR comments, uses an
 *   HTML marker to detect and skip duplicates.
 * - [com.cramsan.agentic.notification.fake.FakeNotifier]: records events in memory for tests.
 */
interface Notifier {
    /** Delivers [event] to the notification channel. Must not throw; failures should be logged. */
    suspend fun notify(event: AgenticEvent)
}
