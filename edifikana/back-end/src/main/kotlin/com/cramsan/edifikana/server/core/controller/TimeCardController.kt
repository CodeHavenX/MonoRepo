package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.TIMECARD_EVENT_ID
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.network.CreateTimeCardEventNetworkRequest
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.TimeCardService
import com.cramsan.framework.core.ktor.HttpResponse
import com.cramsan.framework.utils.time.Chronos
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlin.time.ExperimentalTime

/**
 * Controller for time card related operations.
 */
@OptIn(ExperimentalTime::class)
class TimeCardController(
    private val timeCardService: TimeCardService,
    private val contextRetriever: ContextRetriever,
) {

    /**
     * Handles the creation of a new time card event. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createTimeCardEvent(call: ApplicationCall) = call.handleCall(
        TAG,
        "createTimeCardEvent",
        contextRetriever
    ) { _ ->
        val createTimeCardRequest = call.receive<CreateTimeCardEventNetworkRequest>()

        val newTimeCard = timeCardService.createTimeCardEvent(
            staffId = StaffId(createTimeCardRequest.staffId),
            fallbackStaffName = createTimeCardRequest.fallbackStaffName,
            propertyId = PropertyId(createTimeCardRequest.propertyId),
            type = createTimeCardRequest.type,
            imageUrl = createTimeCardRequest.imageUrl,
            timestamp = Chronos.currentInstant(),
        ).toTimeCardEventNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = newTimeCard,
        )
    }

    /**
     * Handles the retrieval of a time card event. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getTimeCardEvent(call: ApplicationCall) = call.handleCall(
        TAG,
        "getTimeCardEvent",
        contextRetriever,
    ) { _ ->
        val timeCardId = requireNotNull(call.parameters[TIMECARD_EVENT_ID])

        val timeCard = timeCardService.getTimeCardEvent(
            TimeCardEventId(timeCardId),
        )?.toTimeCardEventNetworkResponse()

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
    suspend fun getTimeCardEvents(call: ApplicationCall) = call.handleCall(
        TAG,
        "getTimeCardEvents",
        contextRetriever,
    ) { _ ->
        val staffId = call.request.queryParameters[Routes.TimeCard.QueryParams.STAFF_ID]

        val timeCards = timeCardService.getTimeCardEvents(
            staffId = staffId?.let { StaffId(it) },
        ).map { it.toTimeCardEventNetworkResponse() }

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
                    createTimeCardEvent(call)
                }
                get("{$TIMECARD_EVENT_ID}") {
                    getTimeCardEvent(call)
                }
                get {
                    getTimeCardEvents(call)
                }
            }
        }
    }
}
