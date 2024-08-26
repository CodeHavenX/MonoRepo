package com.codehavenx.alpaca.backend.core.controller

import com.codehavenx.alpaca.shared.api.Routes
import com.cramsan.framework.core.ktor.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route

/**
 * Controller for handling health check requests.
 */
class HealthCheckController {

    /**
     * Handles a health check request.
     */
    suspend fun healthCheck(call: ApplicationCall) = call.handleCall(TAG, "healthCheck") {
        HttpResponse(
            status = HttpStatusCode.OK,
            body = "OK",
        )
    }
    companion object {
        private const val TAG = "HealthCheckController"

        /**
         * Registers the routes for the user controller. The [route] parameter is the root path for the controller.
         */
        fun HealthCheckController.registerRoutes(route: Routing) {
            route.route(Routes.Health.PATH) {
                get {
                    healthCheck(call)
                }
            }
        }
    }
}
