package com.codehavenx.platform.bot.di

import com.codehavenx.platform.bot.config.createJson
import com.codehavenx.platform.bot.controller.kord.DiscordController
import com.codehavenx.platform.bot.controller.kord.InteractionModule
import com.codehavenx.platform.bot.controller.kord.modules.GoogleTranslateInteractionModule
import com.codehavenx.platform.bot.controller.kord.modules.RunasimiInteractionModule
import com.codehavenx.platform.bot.controller.kord.modules.WebhookRegisterInteractionModule
import com.codehavenx.platform.bot.controller.webhook.WebhookController
import com.codehavenx.platform.bot.controller.webhook.WebhookEntryPoint
import com.codehavenx.platform.bot.controller.webhook.entrypoint.GithubCommitPushEntryPoint
import com.codehavenx.platform.bot.service.github.GithubWebhookService
import com.codehavenx.platform.bot.service.google.GoogleTranslateService
import com.codehavenx.platform.bot.service.runasimi.RunasimiService
import com.cramsan.framework.core.ktor.service.DiscordService
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import dev.kord.core.Kord
import io.ktor.client.HttpClient
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the application level components.
 */
val ApplicationModule = module(createdAtStart = true) {

    single(named(DISCORD_BOT_TOKEN)) {
        val config: ApplicationConfig = get()

        config.propertyOrNull("kord.access_token")?.getString() ?: ""
    }

    single(named(RUNASIMI_ENDPOINT_URL)) {
        val config: ApplicationConfig = get()

        config.propertyOrNull("runasimi.endpoint")?.getString() ?: ""
    }

    single {
        runBlocking {
            Kord(get(named(DISCORD_BOT_TOKEN)))
        }
    }

    single<List<WebhookEntryPoint<*>>>(named(LIST_WH_ENTRY_POINTS)) {
        listOf(
            get<GithubCommitPushEntryPoint>(),
        )
    }

    single {
        GithubCommitPushEntryPoint(get(), get())
    }

    single {
        WebhookController(get(named(LIST_WH_ENTRY_POINTS)))
    }

    single<CoroutineScope> {
        GlobalScope
    }

    single {
        DiscordController(get(), get(), get(named(LIST_KORD_INTERACTION_MODULES)))
    }

    single {
        DiscordService(get())
    }

    single {
        GithubWebhookService(get())
    }

    single {
        WebhookRegisterInteractionModule(get())
    }

    single<List<InteractionModule>>(named(LIST_KORD_INTERACTION_MODULES)) {
        listOf(
            get<WebhookRegisterInteractionModule>(),
            get<GoogleTranslateInteractionModule>(),
            get<RunasimiInteractionModule>(),
        )
    }

    single<RunasimiInteractionModule> {
        RunasimiInteractionModule(get())
    }

    single<Json> {
        createJson()
    }

    single {
        GoogleTranslateInteractionModule(get())
    }

    single {
        GoogleTranslateService(get())
    }

    single {
        RunasimiService(
            get(),
            get(named(RUNASIMI_ENDPOINT_URL)),
        )
    }

    single {
        HttpClient {}
    }

    single<Translate> {
        TranslateOptions.getDefaultInstance().service
    }
}

private const val DISCORD_BOT_TOKEN = "DISCORD_BOT_TOKEN"
private const val RUNASIMI_ENDPOINT_URL = "https://runasimi.cramsan.com/"
private const val LIST_WH_ENTRY_POINTS = "LIST_WH_ENTRY_POINTS"
private const val LIST_KORD_INTERACTION_MODULES = "LIST_KORD_INTERACTION_MODULES"
