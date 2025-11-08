package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.unauthenticatedHandler
import com.cramsan.templatereplaceme.api.HealthApi
import com.cramsan.templatereplaceme.lib.model.network.HealthCheckNetworkResponse
import io.ktor.server.routing.Routing

/**
 * Controller for handling health check requests.
 */
@OptIn(NetworkModel::class)
class HealthCheckController(
    private val contextRetriever: ContextRetriever<Unit>
) : Controller {

    /**
     * Handles a health check request.
     */
    fun healthCheck(): HealthCheckNetworkResponse {
        return HealthCheckNetworkResponse("OK")
    }

    /**
     * Registers the routes for the health check controller.
     */
    override fun registerRoutes(route: Routing) {
        HealthApi.register(route) {
            unauthenticatedHandler(api.healthCheck, contextRetriever) { _ ->
                healthCheck()
            }
        }
    }
}
