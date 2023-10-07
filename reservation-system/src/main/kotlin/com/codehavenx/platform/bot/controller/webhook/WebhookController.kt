package com.codehavenx.platform.bot.controller.webhook

import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

class ReservationsController(
) {

    fun registerRoutes(route: Route) {
        route.apply {
            post("getAvailableTimes") {
                getAvailableTimes(call)
            }
        }
    }

    suspend fun getAvailableTimes(call: ApplicationCall) {
        logI(TAG, "handlePost called")

        val result = runCatching {
            val payload = call.receive(entryPoint.type)
            val response = entryPoint.onPayload(payload)
            call.respond(
                response.status,
                response.body,
            )
        }

        if (result.isFailure) {
            logE(TAG, "Unexpected failure when handing request")
        }
    }

    companion object {
        private const val TAG = "ReservationsController"
    }
}
