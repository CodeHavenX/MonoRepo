package com.codehavenx.alpaca.backend.core.controller

import com.codehavenx.alpaca.backend.core.controller.ControllerUtils.handleCall
import com.codehavenx.alpaca.backend.core.service.ReservationService
import com.codehavenx.alpaca.shared.api.CONFIGURATION_ID
import com.codehavenx.alpaca.shared.api.END_DATE
import com.codehavenx.alpaca.shared.api.Routes
import com.codehavenx.alpaca.shared.api.STAFF_ID
import com.codehavenx.alpaca.shared.api.START_DATE
import com.cramsan.framework.core.ktor.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route

/**
 * Controller for availability related operations. CRUD operations for availability.
 */
class AvailabilityController(
    private val reservationService: ReservationService,
) {

    /**
     * Handles the retrieval of available times. The [call] parameter is the request context.
     */
    suspend fun getAvailableTimes(call: ApplicationCall) = call.handleCall(TAG, "getAvailableTimes") {
        val configurationId = requireNotNull(call.request.headers[CONFIGURATION_ID])
        val startDateString = call.request.headers[START_DATE]
        val endDatetimeString = call.request.headers[END_DATE]
        val staffIdString = call.request.headers[STAFF_ID]

        val startDateTime = requireNotNull(startDateString?.toLocalDateTime())
        val endDatetime = requireNotNull(endDatetimeString?.toLocalDateTime())
        val staffId = requireNotNull(staffIdString?.toStaffId())

        val timeSlots = reservationService.getAvailableTimes(
            configurationId,
            startDateTime,
            endDatetime,
            staffId,
        )

        HttpResponse(
            status = HttpStatusCode.OK,
            body = timeSlots,
        )
    }
    companion object {
        private const val TAG = "ReservationsController"

        /**
         * Registers the routes for the user controller. The [route] parameter is the root path for the controller.
         */
        fun AvailabilityController.registerRoutes(route: Routing) {
            route.route(Routes.Availability.PATH) {
                get {
                    getAvailableTimes(call)
                }
            }
        }
    }
}
