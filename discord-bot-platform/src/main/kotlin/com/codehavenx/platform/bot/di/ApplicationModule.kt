package com.codehavenx.platform.bot.di

import dev.kord.core.Kord
import kotlinx.coroutines.runBlocking
import org.kohsuke.github.GitHubBuilder
import org.koin.core.qualifier.named
import org.koin.dsl.module


/**
 * Class to initialize all the application level components.
 */
val ApplicationModule = module {

    single(named(DISCORD_BOT_TOKEN)) { "" }

    single { runBlocking { Kord(get(named(DISCORD_BOT_TOKEN))) } }

    single {
        GitHubBuilder().apply {
            withAppInstallationToken("426c3c290ec641aa485f47ef9d4ef460aba9f49f")
        }.build()
    }
}


private const val DISCORD_BOT_TOKEN = ""