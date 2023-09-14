package com.codehavenx.platform.bot.di

import com.codehavenx.platform.bot.controller.KordController
import com.codehavenx.platform.bot.controller.WebhookController
import com.codehavenx.platform.bot.controller.kord.InteractionModule
import com.codehavenx.platform.bot.controller.kord.WebHookRegisterInteractionModule
import com.codehavenx.platform.bot.service.github.GithubWebhookService
import dev.kord.core.Kord
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the application level components.
 */
val ApplicationModule = module {

    single(named(DISCORD_BOT_TOKEN)) {
        val config: ApplicationConfig = get()

        config.propertyOrNull("kord.access_token")?.getString() ?: ""
    }

    single {
        runBlocking {
            Kord(get(named(DISCORD_BOT_TOKEN)))
        }
    }

    single {
        WebhookController(get(), get())
    }

    single<CoroutineScope> {
        GlobalScope
    }

    single {
        KordController(get(), get(), get())
    }

    single {
        GithubWebhookService(get())
    }

    single {
        WebHookRegisterInteractionModule(get())
    }

    single<List<InteractionModule>> {
        listOf(
            get<WebHookRegisterInteractionModule>()
        )
    }
}

private const val DISCORD_BOT_TOKEN = "DISCORD_BOT_TOKEN"
