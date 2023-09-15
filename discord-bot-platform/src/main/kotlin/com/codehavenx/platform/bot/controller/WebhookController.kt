package com.codehavenx.platform.bot.controller

import com.codehavenx.platform.bot.network.gh.CodePushPayload
import com.codehavenx.platform.bot.service.github.GithubWebhookService
import com.codehavenx.platform.bot.service.github.WebhookEvent
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respondText

class WebhookController(
    private val kordController: KordController,
    private val githubWebhookService: GithubWebhookService,
) {
    suspend fun handleGithubPushPayload(call: ApplicationCall) {
        logI(TAG, "handleGithubPushPayload called")
        val channelId = githubWebhookService.getWebhookEventChannel(WebhookEvent.PUSH)
        if (channelId == null) {
            logW(TAG, "Cannot handle payload. No channel registered.")
        } else {
            logD(TAG, "Responding to handleGithubPushPayload")
            val payload = call.receive<CodePushPayload>()
            val pusher = payload.pusher?.name
            val repoName = payload.repository?.name
            val commitTitle = payload.headCommit?.message
            val commitUrl = payload.headCommit?.url
            kordController.sendMessage(channelId) {
                content = "$pusher pushed a commit to $repoName\n$commitTitle\n$commitUrl"
            }
        }
        call.respondText("Event handled")
    }

    suspend fun handleGithubWorkflowJobsPayload(call: ApplicationCall) {
        logI(TAG, "handleGithubWorkflowJobsPayload called")
        val channelId = githubWebhookService.getWebhookEventChannel(WebhookEvent.WORKFLOW_JOB)
        logD(TAG, "Responding to handleGithubWorkflowJobsPayload")
        if (channelId == null) {
            logW(TAG, "Cannot handle payload. No channel registered.")
        } else {
            logD(TAG, "Responding to handleGithubWorkflowJobsPayload")
        }
        call.respondText("Event handled")
    }

    companion object {
        private const val TAG = "WebhookController"
    }
}
