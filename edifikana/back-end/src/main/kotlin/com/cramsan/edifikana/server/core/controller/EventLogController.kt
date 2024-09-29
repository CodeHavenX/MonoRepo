package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.EVENT_LOG_ENTRY_ID
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.CreateEventLogEntryNetworkRequest
import com.cramsan.edifikana.lib.model.UpdateEventLogEntryNetworkRequest
import com.cramsan.edifikana.server.core.service.EventLogService
import com.cramsan.edifikana.server.core.service.models.EventLogEntryId
import com.cramsan.edifikana.server.core.service.models.StaffId
import com.cramsan.framework.core.ktor.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.datetime.Instant

/**
 * Controller for event log related operations.
 */
class EventLogController(
    private val eventLogService: EventLogService,
) {

    /**
     * Handles the creation of a new event log. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createEventLog(call: ApplicationCall) = call.handleCall(TAG, "createEventLog") {
        val createEventLogRequest = call.receive<CreateEventLogEntryNetworkRequest>()

        val newEventLog = eventLogService.createEventLog(
            staffId = StaffId(createEventLogRequest.staffId),
            time = Instant.fromEpochMilliseconds(createEventLogRequest.time),
            title = createEventLogRequest.title,
        ).toEventLogResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = newEventLog,
        )
    }

    /**
     * Handles the retrieval of an event log. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getEventLog(call: ApplicationCall) = call.handleCall(TAG, "getEventLog") {
        val eventLogId = requireNotNull(call.parameters[EVENT_LOG_ENTRY_ID])

        val eventLog = eventLogService.getEventLog(
            EventLogEntryId(eventLogId),
        )?.toEventLogResponse()

        val statusCode = if (eventLog == null) {
            HttpStatusCode.NotFound
        } else {
            HttpStatusCode.OK
        }

        HttpResponse(
            status = statusCode,
            body = eventLog,
        )
    }

    /**
     * Handles the retrieval of all event logs. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getEventLogs(call: ApplicationCall) = call.handleCall(TAG, "getEventLogs") {
        val eventLogs = eventLogService.getEventLogs().map { it.toEventLogResponse() }

        HttpResponse(
            status = HttpStatusCode.OK,
            body = eventLogs,
        )
    }

    /**
     * Handles the updating of an event log. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateEventLog(call: ApplicationCall) = call.handleCall(TAG, "updateEventLog") {
        val eventLogId = requireNotNull(call.parameters[EVENT_LOG_ENTRY_ID])

        val updateEventLogRequest = call.receive<UpdateEventLogEntryNetworkRequest>()

        val updatedEventLog = eventLogService.updateEventLog(
            id = EventLogEntryId(eventLogId),
            title = updateEventLogRequest.title,
            staffId = StaffId(updateEventLogRequest.staffId),
            time = Instant.fromEpochMilliseconds(updateEventLogRequest.time),
        ).toEventLogResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = updatedEventLog,
        )
    }

    /**
     * Handles the deletion of an event log. The [call] parameter is the request context.
     */
    suspend fun deleteEventLog(call: RoutingCall) {
        val eventLogId = requireNotNull(call.parameters[EVENT_LOG_ENTRY_ID])

        val success = eventLogService.deleteEventLog(
            EventLogEntryId(eventLogId),
        )

        val statusCode = if (success) {
            HttpStatusCode.OK
        } else {
            HttpStatusCode.NotFound
        }

        HttpResponse(
            status = statusCode,
            body = null,
        )
    }

    companion object {
        private const val TAG = "EventLogController"

        /**
         * Registers the routes for the event log controller.
         */
        fun EventLogController.registerRoutes(route: Routing) {
            route.route(Routes.EventLog.PATH) {
                post {
                    createEventLog(call)
                }
                get("{$EVENT_LOG_ENTRY_ID") {
                    getEventLog(call)
                }
                get {
                    getEventLogs(call)
                }
                put {
                    updateEventLog(call)
                }
                delete {
                    deleteEventLog(call)
                }
            }
        }
    }
}
