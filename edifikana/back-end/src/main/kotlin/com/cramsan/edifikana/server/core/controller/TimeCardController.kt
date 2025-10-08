package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.api.TimeCardApi
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.network.CreateTimeCardEventNetworkRequest
import com.cramsan.edifikana.lib.model.network.TimeCardEventListNetworkResponse
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.TimeCardService
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.utils.time.Chronos
import io.ktor.server.routing.Routing
import kotlin.time.ExperimentalTime

/**
 * Controller for time card related operations.
 */
@OptIn(ExperimentalTime::class, NetworkModel::class)
class TimeCardController(
    private val timeCardService: TimeCardService,
    private val contextRetriever: ContextRetriever,
) : Controller {

    /**
     * Creates a new time card event using the provided request data.
     * Returns the created time card event as a network response.
     */
    suspend fun createTimeCardEvent(
        request: CreateTimeCardEventNetworkRequest,
    ): TimeCardEventNetworkResponse {
        val newTimeCard = timeCardService.createTimeCardEvent(
            employeeId = request.employeeId,
            fallbackEmployeeName = request.fallbackEmployeeName,
            propertyId = request.propertyId,
            type = request.type,
            imageUrl = request.imageUrl,
            timestamp = Chronos.currentInstant(),
        ).toTimeCardEventNetworkResponse()
        return newTimeCard
    }

    /**
     * Retrieves a time card event by its [timeCardId].
     * Returns the time card event as a network response, or null if not found.
     */
    suspend fun getTimeCardEvent(
        timeCardId: TimeCardEventId,
    ): TimeCardEventNetworkResponse? {
        return timeCardService.getTimeCardEvent(
            timeCardId,
        )?.toTimeCardEventNetworkResponse()
    }

    /**
     * Retrieves all time card events for the given [employeeId].
     * Returns a list of time card events as a network response.
     */
    suspend fun getTimeCardEvents(
        employeeId: EmployeeId?,
    ): TimeCardEventListNetworkResponse {
        val timeCards = timeCardService.getTimeCardEvents(
            employeeId = employeeId,
        ).map { it.toTimeCardEventNetworkResponse() }
        return TimeCardEventListNetworkResponse(timeCards)
    }

    /**
     * Registers the routes for the time card controller.
     * Sets up the API endpoints and handlers for time card operations.
     */
    override fun registerRoutes(route: Routing) {
        TimeCardApi.register(route) {
            handler(api.createTimeCardEvent, contextRetriever) { request ->
                createTimeCardEvent(request.requestBody)
            }
            handler(api.getTimeCardEvent, contextRetriever) { request ->
                getTimeCardEvent(request.pathParam)
            }
            handler(api.getTimeCardEvents, contextRetriever) { request ->
                getTimeCardEvents(request.queryParam.employeeId)
            }
        }
    }
}
