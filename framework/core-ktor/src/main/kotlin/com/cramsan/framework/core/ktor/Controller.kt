package com.cramsan.framework.core.ktor

import com.cramsan.framework.annotations.BackendController
import io.ktor.server.routing.Routing

/**
 * Base interface for all controllers.
 */
@BackendController
interface Controller {
    /**
     * Register the routes for this controller.
     */
    fun registerRoutes(route: Routing)
}
