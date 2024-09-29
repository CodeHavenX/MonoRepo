package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.TIMECARD_EVENT_ID
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.CreateTimeCardEventNetworkRequest
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.core.service.TimeCardService
import com.cramsan.edifikana.server.core.service.models.StaffId
import com.cramsan.edifikana.server.core.service.models.TimeCardEventId
import com.cramsan.framework.core.ktor.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

/**
 * Controller for time card related operations.
 */
class TimeCardController(
    private val timeCardService: TimeCardService,
) {

    /**
     * Handles the creation of a new time card. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createTimeCard(call: ApplicationCall) = call.handleCall(TAG, "createTimeCard") {
        val createTimeCardRequest = call.receive<CreateTimeCardEventNetworkRequest>()

        val newTimeCard = timeCardService.createTimeCard(
            staffId = StaffId(createTimeCardRequest.staffId),
            eventType = TimeCardEventType.fromString(createTimeCardRequest.type),
        ).toTimeCardResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = newTimeCard,
        )
    }

    /**
     * Handles the retrieval of a time card. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getTimeCard(call: ApplicationCall) = call.handleCall(TAG, "getTimeCard") {
        val timeCardId = requireNotNull(call.parameters[TIMECARD_EVENT_ID])

        val timeCard = timeCardService.getTimeCard(
            TimeCardEventId(timeCardId),
        )?.toTimeCardResponse()

        val statusCode = if (timeCard == null) {
            HttpStatusCode.NotFound
        } else {
            HttpStatusCode.OK
        }

        HttpResponse(
            status = statusCode,
            body = timeCard,
        )
    }

    /**
     * Handles the retrieval of all time cards. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getTimeCards(call: ApplicationCall) = call.handleCall(TAG, "getTimeCards") {
        val timeCards = timeCardService.getTimeCards().map { it.toTimeCardResponse() }

        HttpResponse(
            status = HttpStatusCode.OK,
            body = timeCards,
        )
    }

    companion object {
        private const val TAG = "TimeCardController"

        /**
         * Registers the routes for the time card controller.
         */
        fun TimeCardController.registerRoutes(route: Routing) {
            route.route(Routes.TimeCard.PATH) {
                post {
                    createTimeCard(call)
                }
                get("{$TIMECARD_EVENT_ID") {
                    getTimeCard(call)
                }
                get {
                    getTimeCards(call)
                }
            }
        }
    }
}
