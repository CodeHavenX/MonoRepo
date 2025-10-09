package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.api.EventLogApi
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.network.CreateEventLogEntryNetworkRequest
import com.cramsan.edifikana.lib.model.network.EventLogEntryListNetworkResponse
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdateEventLogEntryNetworkRequest
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.EventLogService
import com.cramsan.edifikana.server.core.service.authorization.RBACService
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.UnauthorizedException
import io.ktor.server.routing.Routing
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Controller for event log related operations.
 */
class EventLogController(
    private val eventLogService: EventLogService,
    private val rbacService: RBACService,
    private val contextRetriever: ContextRetriever,
) : Controller {

    /**
     * Handles the creation of a new event log entry.
     * Creates an event log entry using the provided [createEventLogRequest] and returns the
     * created entry as a network response.
     */
    @OptIn(NetworkModel::class, ExperimentalTime::class)
    suspend fun createEventLogEntry(
        request: OperationRequest<
            CreateEventLogEntryNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext
            >,
    ): EventLogEntryNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.propertyId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(
                "User does not have permission to create log events " +
                    "for property ${request.requestBody.propertyId}"
            )
        }

        val createEventLogRequest = request.requestBody
        val newEventLog = eventLogService.createEventLogEntry(
            employeeId = createEventLogRequest.employeeId,
            fallbackEmployeeName = createEventLogRequest.fallbackEmployeeName,
            propertyId = createEventLogRequest.propertyId,
            type = createEventLogRequest.type,
            fallbackEventType = createEventLogRequest.fallbackEventType,
            timestamp = Instant.fromEpochSeconds(createEventLogRequest.timestamp),
            title = createEventLogRequest.title,
            description = createEventLogRequest.description,
            unit = createEventLogRequest.unit,
        ).toEventLogEntryNetworkResponse()

        return newEventLog
    }

    /**
     * Handles the retrieval of a single event log entry.
     * Returns the event log entry identified by [eventLogId] as a network response, or null if not found.
     */
    @OptIn(NetworkModel::class)
    suspend fun getEventLogEntry(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            EventLogEntryId,
            ClientContext.AuthenticatedClientContext
            >,
    ): EventLogEntryNetworkResponse? {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.EMPLOYEE)) {
            throw UnauthorizedException("User does not have permission to view event log entry ${request.pathParam}")
        }

        val eventLogId = request.pathParam
        val eventLog = eventLogService.getEventLogEntry(
            eventLogId,
        )?.toEventLogEntryNetworkResponse()

        return eventLog
    }

    /**
     * Handles the retrieval of all event log entries. The [call] parameter is the request context.
     */
    @Suppress("UnusedParameter")
    @OptIn(NetworkModel::class)
    fun getEventLogEntries(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext
            >
    ): EventLogEntryListNetworkResponse {
        TODO("This function is not yet implemented")
    }

    /**
     * Handles the updating of an event log entry. Updates the event log entry identified by [eventLogId] with
     * the data provided in [updateEventLogRequest].
     * Returns the updated event log entry as a network response.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateEventLogEntry(
        request: OperationRequest<
            UpdateEventLogEntryNetworkRequest,
            NoQueryParam,
            EventLogEntryId,
            ClientContext.AuthenticatedClientContext
            >,
    ): EventLogEntryNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.EMPLOYEE)) {
            throw UnauthorizedException("User does not have permission to update event log entry ${request.pathParam}")
        }
        val updateEventLogRequest = request.requestBody
        val eventLogId = request.pathParam
        return eventLogService.updateEventLogEntry(
            id = eventLogId,
            type = updateEventLogRequest.type,
            fallbackEventType = updateEventLogRequest.fallbackEventType,
            title = updateEventLogRequest.title,
            description = updateEventLogRequest.description,
            unit = updateEventLogRequest.unit,
        ).toEventLogEntryNetworkResponse()
    }

    /**
     * Handles the deletion of an event log entry. Deletes the event log entry identified by [eventLogId].
     * Returns [NoResponseBody] to indicate successful deletion.
     */
    suspend fun deleteEventLogEntry(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            EventLogEntryId,
            ClientContext.AuthenticatedClientContext
            >,
    ): NoResponseBody {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.EMPLOYEE)) {
            throw UnauthorizedException("User does not have permission to delete event log entry ${request.pathParam}")
        }
        val eventLogId = request.pathParam
        eventLogService.deleteEventLogEntry(
            eventLogId,
        )
        return NoResponseBody
    }

    /**
     * Registers the routes for the event log controller.
     * Sets up the API endpoints and handlers for event log operations.
     */
    @OptIn(NetworkModel::class)
    override fun registerRoutes(route: Routing) {
        EventLogApi.register(route) {
            handler(api.createEventLogEntry, contextRetriever) { request ->
                createEventLogEntry(request)
            }
            handler(api.getEventLogEntry, contextRetriever) { request ->
                getEventLogEntry(request)
            }
            handler(api.getEventLogEntries, contextRetriever) { request ->
                getEventLogEntries(request)
            }
            handler(api.updateEventLogEntry, contextRetriever) { request ->
                updateEventLogEntry(request)
            }
            handler(api.deleteEventLogEntry, contextRetriever) { request ->
                deleteEventLogEntry(request)
            }
        }
    }
}
