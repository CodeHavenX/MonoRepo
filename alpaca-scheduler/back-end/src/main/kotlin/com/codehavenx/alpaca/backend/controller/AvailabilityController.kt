package com.codehavenx.alpaca.backend.controller

import com.codehavenx.alpaca.backend.service.ReservationService
import com.codehavenx.alpaca.shared.api.Routes
import com.codehavenx.alpaca.shared.api.controller.CONFIGURATION_ID
import com.codehavenx.alpaca.shared.api.controller.END_DATE
import com.codehavenx.alpaca.shared.api.controller.STAFF_ID
import com.codehavenx.alpaca.shared.api.controller.START_DATE
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
) : BaseController() {

    /**
     * Registers the routes for the user controller. The [route] parameter is the root path for the controller.
     */
    override fun registerRoutes(route: Routing) {
        route.route(Routes.Availability.PATH) {
            get {
                getAvailableTimes(call)
            }
        }
    }

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
    }
}
