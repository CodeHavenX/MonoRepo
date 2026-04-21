package com.cramsan.flyerboard.server.controller

import com.cramsan.flyerboard.api.HealthApi
import com.cramsan.flyerboard.lib.model.network.HealthCheckNetworkResponse
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.unauthenticatedHandler
import io.ktor.server.routing.Routing

/**
 * Controller for the health check endpoint.
 */
@OptIn(NetworkModel::class)
class HealthController(
    private val contextRetriever: ContextRetriever<FlyerBoardContextPayload>,
) : Controller {

    override fun registerRoutes(route: Routing) {
        HealthApi.register(route) {
            unauthenticatedHandler(api.check, contextRetriever) { _ ->
                HealthCheckNetworkResponse(message = "ok")
            }
        }
    }
}
