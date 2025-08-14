package com.cramsan.discordbot.di

import com.cramsan.discordbot.config.AppConfig
import com.cramsan.discordbot.config.createJson
import com.cramsan.discordbot.config.loadConfig
import com.cramsan.discordbot.discord.DiscordBotService
import com.cramsan.discordbot.github.GitHubService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/**
 * Class to initialize all the application level components.
 */
val ApplicationModule = module {

    single<CoroutineScope> {
        GlobalScope
    }

    single<Json> {
        createJson()
    }

    single<AppConfig> {
        loadConfig()
    }

    single<GitHubService> {
        GitHubService(get(), get())
    }

    single<DiscordBotService> {
        DiscordBotService(get(), get(), get())
    }
}