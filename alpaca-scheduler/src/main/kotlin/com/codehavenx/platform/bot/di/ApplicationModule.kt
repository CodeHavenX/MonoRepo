package com.codehavenx.platform.bot.di

import com.codehavenx.platform.bot.config.createJson
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import dev.kord.core.Kord
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

    single {
        runBlocking {
            Kord(get(named(DISCORD_BOT_TOKEN)))
        }
    }

    single<CoroutineScope> {
        GlobalScope
    }

    single<Json> {
        createJson()
    }

    single<Translate> {
        TranslateOptions.getDefaultInstance().getService()
    }
}

private const val DISCORD_BOT_TOKEN = "DISCORD_BOT_TOKEN"
private const val LIST_WH_ENTRY_POINTS = "LIST_WH_ENTRY_POINTS"
private const val LIST_KORD_INTERACTION_MODULES = "LIST_KORD_INTERACTION_MODULES"
