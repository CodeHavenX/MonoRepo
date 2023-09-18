package com.codehavenx.platform.bot.controller.webhook

import com.codehavenx.platform.bot.controller.kord.DiscordController
import com.codehavenx.platform.bot.network.gh.CodePushPayload
import com.codehavenx.platform.bot.service.github.GithubWebhookService
import com.codehavenx.platform.bot.service.github.WebhookEvent
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
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

    override suspend fun onPayload(payload: CodePushPayload, call: ApplicationCall) {
        logI(TAG, "handleGithubPushPayload called")
        val channelId = githubWebhookService.getWebhookEventChannel(WebhookEvent.PUSH)
        if (channelId == null) {
            logW(TAG, "Cannot handle payload. No channel registered.")
            call.respond(HttpStatusCode.InternalServerError, "Event unhandled")
        } else {
            logD(TAG, "Responding to handleGithubPushPayload")
            val pusher = payload.pusher?.name
            val repoName = payload.repository?.name
            val commitTitle = payload.headCommit?.message
            val commitUrl = payload.headCommit?.url
            discordController.sendMessage(channelId) {
                content = "$pusher pushed a commit to $repoName\n$commitTitle\n$commitUrl"
            }
            call.respondText("Event handled")
        }
    }

    companion object {
        private const val TAG = "GithubCommitPushEntryPoint"
    }
}
