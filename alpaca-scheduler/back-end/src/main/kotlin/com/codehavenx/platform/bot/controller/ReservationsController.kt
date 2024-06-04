package com.codehavenx.platform.bot.controller

import com.codehavenx.platform.bot.service.ReservationService
import com.cramsan.framework.core.ktor.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall

class ReservationsController(
    private val reservationService: ReservationService,
) {

    suspend fun getAvailableTimes(call: ApplicationCall) = call.handleCall(TAG, "getAvailableTimes") {
        val configurationId = requireNotNull(call.request.headers["configuration_id"])
        val startDateString = call.request.headers["start_date"]
        val endDatetimeString = call.request.headers["end_date"]
        val staffIdString = call.request.headers["staff_id"]

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
