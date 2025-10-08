package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.api.HealthApi
import com.cramsan.edifikana.lib.model.network.HealthCheckNetworkResponse
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.OperationHandler.register
import io.ktor.server.routing.Routing

/**
 * Controller for handling health check requests.
 */
@OptIn(NetworkModel::class)
class HealthCheckController(
    private val contextRetriever: ContextRetriever,
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
