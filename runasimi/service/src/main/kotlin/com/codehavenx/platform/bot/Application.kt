package com.codehavenx.platform.bot

import com.codehavenx.platform.bot.controller.ApiController
import com.codehavenx.platform.bot.controller.HtmlController
import com.codehavenx.platform.bot.di.ApplicationModule
import com.codehavenx.platform.bot.di.FrameworkModule
import com.codehavenx.platform.bot.di.createKtorModule
import com.cramsan.framework.logging.logI
import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.partialcontent.PartialContent
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() = runBlocking {
    initializeDependencies()
    startServer()
}

fun Application.startServer() = runBlocking {
    configureKtorEngine()

    val webhookController: ApiController by inject()
    val htmlController: HtmlController by inject()

    configureEntryPoints(webhookController, htmlController)

    startApplication()
}

/**
 * Configures the Ktor engine.
 */
fun Application.configureKtorEngine() {
    install(CallLogging)
    install(PartialContent)
    install(ContentNegotiation)
    install(AutoHeadResponse)
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
}

/**
 * Configures all the system entry points. This includes HTML routes and API endpoints.
 */
suspend fun Application.configureEntryPoints(
    apiController: ApiController,
    htmlController: HtmlController,
) {
    routing {
        apiController.registerRoutes(this@routing)
        htmlController.registerRoutes(this@routing)
    }
}

/**
 * Initialize the service dependencies.
 */
fun Application.initializeDependencies() {
    startKoin {
        modules(
            createKtorModule(this@initializeDependencies),
            FrameworkModule,
            ApplicationModule,
        )
    }
}

/**
 * Let's do it!
 */
fun Application.startApplication() {
    logI(TAG, "Application is ready.")
}

private const val TAG = "Application"
