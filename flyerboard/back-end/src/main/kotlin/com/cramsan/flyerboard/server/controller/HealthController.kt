package com.cramsan.flyerboard.server.controller

import com.cramsan.flyerboard.api.HealthApi
import com.cramsan.flyerboard.lib.model.network.HealthCheckNetworkResponse
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.framework.annotations.BackendController
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.handler
import io.ktor.server.routing.Routing

/**
 * Controller for the health check endpoint.
 */
@BackendController
class HealthController : Controller {
    override fun registerRoutes(route: Routing) {
        HealthApi.register(route, FlyerBoardContextPayload::class) {
            handler(api.check) { _ ->
                HealthCheckNetworkResponse(message = "ok")
            }
        }
    }
}
