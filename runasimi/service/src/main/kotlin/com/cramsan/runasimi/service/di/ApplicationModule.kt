package com.cramsan.runasimi.service.di

import com.cramsan.framework.core.ktor.service.DiscordService
import com.cramsan.runasimi.service.controller.ApiController
import com.cramsan.runasimi.service.controller.HtmlController
import com.cramsan.runasimi.service.service.TextToSpeechService
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
val ApplicationModule = module(createdAtStart = true) {

    single(named(CONTAINER_PORT)) {
        val config: ApplicationConfig = get()

        config.property("docker.container_port").getString()
    }

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
        ApiController(get())
    }

    single {
        DiscordService(get())
    }

    single {
        HtmlController()
    }

    single<CoroutineScope> {
        GlobalScope
    }

    single {
        TextToSpeechService(
            get(),
            get(named(CONTAINER_PORT)),
        )
    }
}
internal const val CONTAINER_PORT = "CONTAINER_PORT"
private const val DISCORD_BOT_TOKEN = "DISCORD_BOT_TOKEN"
