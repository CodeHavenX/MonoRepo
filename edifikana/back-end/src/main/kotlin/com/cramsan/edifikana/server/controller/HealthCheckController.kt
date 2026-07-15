package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.HealthApi
import com.cramsan.edifikana.lib.model.network.health.HealthCheckNetworkResponse
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.framework.annotations.BackendController
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.unauthenticatedHandler
import io.ktor.server.routing.Routing

/**
 * Controller for handling health check requests.
 */
@BackendController
class HealthCheckController : Controller {
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
        HealthApi.register(route, SupabaseContextPayload::class) {
            unauthenticatedHandler(api.healthCheck) { _ ->
                healthCheck()
            }
        }
    }
}
