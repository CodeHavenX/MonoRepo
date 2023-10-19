package com.codehavenx.platform.bot.di

import com.codehavenx.platform.bot.controller.ApiController
import com.codehavenx.platform.bot.controller.HtmlController
import com.codehavenx.platform.bot.service.TextToSpeechService
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

        config.property("docker.container_port").getString()
    }

    single {
        ApiController(get())
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
private const val CONTAINER_PORT = "CONTAINER_PORT"
