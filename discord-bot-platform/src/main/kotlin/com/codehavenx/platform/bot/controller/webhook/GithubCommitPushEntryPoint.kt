package com.codehavenx.platform.bot.controller.webhook

import com.codehavenx.platform.bot.controller.kord.DiscordController
import com.codehavenx.platform.bot.ktor.HttpResponse
import com.codehavenx.platform.bot.network.gh.CodePushPayload
import com.codehavenx.platform.bot.service.github.GithubWebhookService
import com.codehavenx.platform.bot.service.github.WebhookEvent
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass

/**
 * Webhook entry point to handle Github push commits.
 */
class GithubCommitPushEntryPoint(
    private val githubWebhookService: GithubWebhookService,
    private val discordController: DiscordController,
) : WebhookEntryPoint<CodePushPayload> {

    override val type: KClass<CodePushPayload> = CodePushPayload::class
    override val path: String = "github/push"

    override suspend fun onPayload(payload: CodePushPayload): HttpResponse {
        logI(TAG, "handleGithubPushPayload called")
        val channelId = githubWebhookService.getWebhookEventChannel(WebhookEvent.PUSH)
        return if (channelId.isNullOrBlank()) {
            logW(TAG, "Cannot handle payload. No channel registered.")
            HttpResponse(
                status = HttpStatusCode.InternalServerError,
                body = "Event unhandled",
            )
        } else {
            logD(TAG, "Responding to handleGithubPushPayload")
            val pusher = payload.pusher?.name
            val repoName = payload.repository?.name
            val commitTitle = payload.headCommit?.message
            val commitUrl = payload.headCommit?.url
            discordController.sendMessage(channelId) {
                content = "$pusher pushed a commit to $repoName\n$commitTitle\n$commitUrl"
            }
            HttpResponse(
                status = HttpStatusCode.OK,
                body = "Event handled",
            )
        }
    }

    companion object {
        private const val TAG = "GithubCommitPushEntryPoint"
    }
}
