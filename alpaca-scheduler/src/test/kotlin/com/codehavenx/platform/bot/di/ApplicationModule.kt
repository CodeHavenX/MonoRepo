package com.codehavenx.platform.bot.di

import com.codehavenx.platform.bot.config.createJson
import com.codehavenx.platform.bot.controller.kord.InteractionModule
import com.codehavenx.platform.bot.controller.kord.DiscordController
import com.codehavenx.platform.bot.controller.kord.modules.WebhookRegisterInteractionModule
import com.codehavenx.platform.bot.controller.webhook.entrypoint.GithubCommitPushEntryPoint
import com.codehavenx.platform.bot.controller.webhook.WebhookController
import com.codehavenx.platform.bot.controller.webhook.WebhookEntryPoint
import com.codehavenx.platform.bot.service.DiscordService
import com.codehavenx.platform.bot.service.github.GithubWebhookService
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the application level components.
 */
fun createApplicationModule(
    discordController: DiscordController = mockk(relaxed = true),
    discordService: DiscordService = mockk(relaxed = true),
    githubWebhookService: GithubWebhookService = mockk(relaxed = true),
    webHookRegisterInteractionModule: WebhookRegisterInteractionModule = mockk(relaxed = true),
    githubCommitPushEntryPoint: GithubCommitPushEntryPoint = mockk(relaxed = true),
) = module(createdAtStart = true) {

    single<List<WebhookEntryPoint<*>>>(named(LIST_WH_ENTRY_POINTS)) {
        listOf(
            get<GithubCommitPushEntryPoint>()
        )
    }

    single {
        githubCommitPushEntryPoint
    }

    single {
        discordService
    }

    single {
        WebhookController(get(named(LIST_WH_ENTRY_POINTS)))
    }

    single {
        discordController
    }

    single {
        githubWebhookService
    }

    single {
        webHookRegisterInteractionModule
    }

    single<List<InteractionModule>>(named(LIST_KORD_INTERACTION_MODULES)) {
        listOf(
            get<WebhookRegisterInteractionModule>()
        )
    }

    single<Json> {
        createJson()
    }
}

private const val LIST_WH_ENTRY_POINTS = "LIST_WH_ENTRY_POINTS"
private const val LIST_KORD_INTERACTION_MODULES = "LIST_KORD_INTERACTION_MODULES"
