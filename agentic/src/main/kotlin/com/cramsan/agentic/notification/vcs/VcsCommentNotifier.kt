package com.cramsan.agentic.notification.vcs

import com.cramsan.agentic.notification.AgenticEvent
import com.cramsan.agentic.notification.Notifier
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW

private const val TAG = "VcsCommentNotifier"
private const val NOTIFICATION_MARKER = "<!-- agentic-notification -->"

class VcsCommentNotifier(private val vcsProvider: VcsProvider) : Notifier {

    override suspend fun notify(event: AgenticEvent) {
        when (event) {
            is AgenticEvent.TaskFailed -> handleTaskFailed(event)
            is AgenticEvent.RunDeadlocked -> handleRunDeadlocked(event)
            is AgenticEvent.RunCompleted -> handleRunCompleted(event)
        }
    }

    private suspend fun handleTaskFailed(event: AgenticEvent.TaskFailed) {
        logD(TAG, "handleTaskFailed called for task: ${event.task.id} (${event.task.title})")
        val branchName = "agentic/${event.task.id}"
        val openPrs = vcsProvider.listOpenPullRequests(labels = listOf("agentic-code"))
        val pr = openPrs.firstOrNull { it.sourceBranch == branchName }
        if (pr == null) {
            logW(TAG, "No open PR found for failed task ${event.task.id}, cannot post notification comment")
            return
        }
        val comment = formatNotificationComment(
            "Task Failed",
            "Task **${event.task.title}** (${event.task.id}) failed with reason:\n\n${event.reason}",
        )
        postCommentIfNotDuplicate(pr.id, comment)
    }

    private suspend fun handleRunDeadlocked(event: AgenticEvent.RunDeadlocked) {
        logD(TAG, "handleRunDeadlocked called. Blocked tasks: ${event.blockedTasks.size}, failed tasks: ${event.failedTasks.size}")
        val openPrs = vcsProvider.listOpenPullRequests()
        val pr = openPrs.maxByOrNull { it.id } ?: run {
            logW(TAG, "No open PR found to post deadlock notification")
            return
        }
        val blockedIds = event.blockedTasks.joinToString(", ") { it.id }
        val failedIds = event.failedTasks.joinToString(", ") { it.id }
        val comment = formatNotificationComment(
            "Run Deadlocked",
            "The agentic run is deadlocked and cannot make further progress.\n\nBlocked tasks: $blockedIds\nFailed tasks: $failedIds",
        )
        postCommentIfNotDuplicate(pr.id, comment)
    }

    private fun handleRunCompleted(event: AgenticEvent.RunCompleted) {
        logD(TAG, "handleRunCompleted called. Completed tasks: ${event.completedTasks.size}")
        logI(TAG, "Agentic run completed. ${event.completedTasks.size} tasks completed.")
    }

    private suspend fun postCommentIfNotDuplicate(prId: String, comment: String) {
        val existingComments = vcsProvider.getPullRequestComments(prId)
        if (existingComments.any { it.body.contains(NOTIFICATION_MARKER) }) {
            logW(TAG, "Notification comment already exists on PR $prId, skipping")
            return
        }
        vcsProvider.addPullRequestComment(prId, comment)
        logI(TAG, "Notification comment successfully posted to PR $prId")
    }

    private fun formatNotificationComment(title: String, body: String): String {
        return """$NOTIFICATION_MARKER
## :robot: Agentic Notification: $title

$body
"""
    }
}
