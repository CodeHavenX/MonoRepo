package com.codehavenx.platform.bot.controller.webhook

import com.cramsan.framework.logging.logI
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

class ReservationsController {

    fun registerRoutes(route: Route) {
        route.apply {
            post("getAvailableTimes") {
                getAvailableTimes(call)
            }
        }
    }

    suspend fun getAvailableTimes(call: ApplicationCall) {
        logI(TAG, "handlePost called")
    }

    companion object {
        private const val TAG = "ReservationsController"
    }
}
