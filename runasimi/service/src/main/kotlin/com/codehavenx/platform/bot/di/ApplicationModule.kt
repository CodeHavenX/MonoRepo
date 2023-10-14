package com.codehavenx.platform.bot.di

import com.codehavenx.platform.bot.controller.webhook.WebhookController
import com.codehavenx.platform.bot.service.TranslationService
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the application level components.
 */
val ApplicationModule = module(createdAtStart = true) {

    single(named(CONTAINER_PORT)) {
        val config: ApplicationConfig = get()

        config.property("kord.container_port").getString()
    }

    single {
        WebhookController(get())
    }

    single<CoroutineScope> {
        GlobalScope
    }

    single {
        TranslationService(
            get(),
            get(named(CONTAINER_PORT)),
        )
    }
}
private const val CONTAINER_PORT = "CONTAINER_PORT"

