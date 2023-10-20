package com.cramsan.runasimi.service.di

import com.cramsan.runasimi.service.controller.ApiController
import com.cramsan.runasimi.service.controller.HtmlController
import com.cramsan.runasimi.service.service.TextToSpeechService
import com.cramsan.framework.core.ktor.service.DiscordService
import io.ktor.server.config.ApplicationConfig
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the application level components.
 */
fun createApplicationModule(
    discordService: DiscordService = mockk(relaxed = true),
    apiController: ApiController = mockk(relaxed = true),
    htmlController: HtmlController = mockk(relaxed = true),
    textToSpeechService: TextToSpeechService = mockk(relaxed = true),
    scope: CoroutineScope,
) = module(createdAtStart = true) {

    single {
        discordService
    }

    single {
        apiController
    }

    single {
        ApiController(get())
    }

    single {
        htmlController
    }

    single<CoroutineScope> {
        scope
    }

    single {
        textToSpeechService
    }

}