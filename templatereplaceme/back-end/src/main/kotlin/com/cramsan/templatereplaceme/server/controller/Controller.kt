package com.cramsan.templatereplaceme.server.controller

import io.ktor.server.routing.Routing

/**
 * Base interface for all controllers.
 */
interface Controller {

    /**
     * Register the routes for this controller.
     */
    fun registerRoutes(route: Routing)
}
