package com.codehavenx.platform.bot.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

/**
 * This controller will handle and route requests for HTML content.
 */
class HtmlController {

    /**
     * This function will register the routes for all HTML resources.
     */
    fun registerRoutes(route: Route) {
        route.apply {
            get {
                showIndexPage(call)
            }
        }
    }

    /**
     * Show the home page.
     */
    private suspend fun showIndexPage(call: ApplicationCall) {
        call.respond(FreeMarkerContent("index.ftl", mapOf<String, String>()))
    }
}
