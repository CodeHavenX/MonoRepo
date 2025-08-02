package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.EVENT_LOG_ENTRY_ID
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.network.CreateEventLogEntryNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateEventLogEntryNetworkRequest
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.EventLogService
import com.cramsan.framework.annotations.NetworkModel
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
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Controller for event log related operations.
 */
class EventLogController(
    private val eventLogService: EventLogService,
    private val contextRetriever: ContextRetriever,
) {

    /**
     * Handles the creation of a new event log entry. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class, ExperimentalTime::class)
    suspend fun createEventLogEntry(
        call: ApplicationCall,
    ) = call.handleCall(TAG, "createEventLogEntry", contextRetriever) { _ ->
        val createEventLogRequest = call.receive<CreateEventLogEntryNetworkRequest>()

        val newEventLog = eventLogService.createEventLogEntry(
            staffId = createEventLogRequest.staffId?.let { StaffId(it) },
            fallbackStaffName = createEventLogRequest.fallbackStaffName,
            propertyId = PropertyId(createEventLogRequest.propertyId),
            type = createEventLogRequest.type,
            fallbackEventType = createEventLogRequest.fallbackEventType,
            timestamp = Instant.fromEpochSeconds(createEventLogRequest.timestamp),
            title = createEventLogRequest.title,
            description = createEventLogRequest.description,
            unit = createEventLogRequest.unit,
        ).toEventLogEntryNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = newEventLog,
        )
    }

    /**
     * Handles the retrieval of an event log. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getEventLogEntry(
        call: ApplicationCall,
    ) = call.handleCall(TAG, "getEventLogEntry", contextRetriever) { _ ->
        val eventLogId = requireNotNull(call.parameters[EVENT_LOG_ENTRY_ID])

        val eventLog = eventLogService.getEventLogEntry(
            EventLogEntryId(eventLogId),
        )?.toEventLogEntryNetworkResponse()

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
     * Handles the retrieval of all event log entries. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getEventLogEntries(
        call: ApplicationCall,
    ) = call.handleCall(TAG, "getEventLogEntries", contextRetriever) { _ ->
        val eventLogs = eventLogService.getEventLogEntries().map { it.toEventLogEntryNetworkResponse() }

        HttpResponse(
            status = HttpStatusCode.OK,
            body = eventLogs,
        )
    }

    /**
     * Handles the updating of an event log entry. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateEventLogEntry(
        call: ApplicationCall,
    ) = call.handleCall(TAG, "updateEventLogEntry", contextRetriever) { _ ->
        val eventLogId = requireNotNull(call.parameters[EVENT_LOG_ENTRY_ID])

        val updateEventLogRequest = call.receive<UpdateEventLogEntryNetworkRequest>()

        val updatedEventLog = eventLogService.updateEventLogEntry(
            id = EventLogEntryId(eventLogId),
            type = updateEventLogRequest.type,
            fallbackEventType = updateEventLogRequest.fallbackEventType,
            title = updateEventLogRequest.title,
            description = updateEventLogRequest.description,
            unit = updateEventLogRequest.unit,
        ).toEventLogEntryNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = updatedEventLog,
        )
    }

    /**
     * Handles the deletion of an event log entry. The [call] parameter is the request context.
     */
    suspend fun deleteEventLogEntry(
        call: RoutingCall,
    ) = call.handleCall(TAG, "deleteEventLogEntry", contextRetriever) { _ ->
        val eventLogId = requireNotNull(call.parameters[EVENT_LOG_ENTRY_ID])

        val success = eventLogService.deleteEventLogEntry(
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
                    createEventLogEntry(call)
                }
                get("{$EVENT_LOG_ENTRY_ID}") {
                    getEventLogEntry(call)
                }
                get {
                    getEventLogEntries(call)
                }
                put("{$EVENT_LOG_ENTRY_ID}") {
                    updateEventLogEntry(call)
                }
                delete("{$EVENT_LOG_ENTRY_ID}") {
                    deleteEventLogEntry(call)
                }
            }
        }
    }
}
