package com.cramsan.discordbot

import com.cramsan.discordbot.config.AppConfig
import com.cramsan.discordbot.di.ApplicationModule
import com.cramsan.discordbot.discord.DiscordBotService
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject

/**
 * Main entry point of the application, used only during testing/local development.
 */
fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

/**
 * Production entry point of the application.
 */
fun Application.module() = runBlocking {
    initializeDependencies()
    startServer()
}

/**
 * Starts the server.
 */
fun Application.startServer() = runBlocking {
    configureKtorEngine()
    configureEntryPoints()
    startDiscordBot()
}

/**
 * Configures the Ktor engine.
 */
fun Application.configureKtorEngine() {
    val json: Json by inject()

    install(CallLogging)
    install(ContentNegotiation) {
        json(json)
    }
}

/**
 * Initialize the service dependencies.
 */
fun Application.initializeDependencies() {
    startKoin {
        modules(
            ApplicationModule,
        )
    }
}

/**
 * Configures the entry points of the application.
 */
fun Application.configureEntryPoints() {
    val config: AppConfig by inject()

    routing {
        get("/") {
            call.respondText("Discord Bot is running! 🤖")
        }

        get("/health") {
            call.respondText("OK")
        }

        get("/status") {
            call.respondText(
                """
                Discord Bot Status:
                - GitHub Owner: ${config.githubOwner}
                - GitHub Repo: ${config.githubRepo}
                - Server Port: ${config.serverPort}
                """.trimIndent()
            )
        }
    }
}

/**
 * Starts the Discord bot.
 */
fun Application.startDiscordBot() = runBlocking {
    val discordBotService: DiscordBotService by inject()

    // Start the Discord bot
    discordBotService.start()

    // Add shutdown hook to properly close the bot
    Runtime.getRuntime().addShutdownHook(
        Thread {
            runBlocking {
                discordBotService.stop()
            }
        }
    )
}
