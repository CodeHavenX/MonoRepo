package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.TimeCardApi
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.network.CreateTimeCardEventNetworkRequest
import com.cramsan.edifikana.lib.model.network.GetTimeCardEventsQueryParams
import com.cramsan.edifikana.lib.model.network.TimeCardEventListNetworkResponse
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.edifikana.server.controller.authentication.ClientContext
import com.cramsan.edifikana.server.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.service.TimeCardService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.utils.exceptions.UnauthorizedException
import com.cramsan.framework.utils.time.Chronos
import io.ktor.server.routing.Routing
import kotlin.time.ExperimentalTime

/**
 * Controller for time card related operations.
 */
@OptIn(ExperimentalTime::class, NetworkModel::class)
class TimeCardController(
    private val timeCardService: TimeCardService,
    private val rbacService: RBACService,
    private val contextRetriever: ContextRetriever,
) : Controller {

    /**
     * Handles the creation of a new time card event. Validates the request and user permissions,
     * then creates the time card event using the [timeCardService].
     * Returns the created time card event as a network response.
     */
    suspend fun createTimeCardEvent(
        request: OperationRequest<
            CreateTimeCardEventNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext
            >,
    ): TimeCardEventNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.propertyId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(
                "User does not have permission to create time card events " +
                    "for property ${request.requestBody.propertyId}"
            )
        }

        val newTimeCard = timeCardService.createTimeCardEvent(
            employeeId = request.requestBody.employeeId,
            fallbackEmployeeName = request.requestBody.fallbackEmployeeName,
            propertyId = request.requestBody.propertyId,
            type = request.requestBody.type,
            imageUrl = request.requestBody.imageUrl,
            timestamp = Chronos.currentInstant(),
        ).toTimeCardEventNetworkResponse()
        return newTimeCard
    }

    /**
     * Retrieves a time card event by its [TimeCardEventId]. Validates user permissions before fetching.
     * Returns the time card event as a network response, or null if not found.
     */
    suspend fun getTimeCardEvent(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            TimeCardEventId,
            ClientContext.AuthenticatedClientContext
            >,
    ): TimeCardEventNetworkResponse? {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.EMPLOYEE)) {
            throw UnauthorizedException("User does not have permission to view time card event ${request.pathParam}")
        }

        return timeCardService.getTimeCardEvent(
            request.pathParam,
        )?.toTimeCardEventNetworkResponse()
    }

    /**
     * Retrieves a list of time card events based on the provided query parameters.
     * Validates user permissions before fetching the events.
     * Returns a list of time card events as a network response.
     */
    @Suppress("ThrowsCount")
    suspend fun getTimeCardEvents(
        request: OperationRequest<
            NoRequestBody,
            GetTimeCardEventsQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext
            >,
    ): TimeCardEventListNetworkResponse {
        val targetEmployeeId = request.queryParam.employeeId
        val targetPropertyId = request.queryParam.propertyId

        targetEmployeeId?.let {
            if (!rbacService.hasRoleOrHigher(request.context, it, UserRole.EMPLOYEE)) {
                throw UnauthorizedException(
                    "User does not have permission to view time card events for employee " +
                        "${request.queryParam.employeeId}"
                )
            }
        }

        if (!rbacService.hasRoleOrHigher(request.context, targetPropertyId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(
                "User does not have permission to view time card events for property " +
                    "${request.queryParam.propertyId}"
            )
        }

        val timeCards = timeCardService.getTimeCardEvents(
            employeeId = request.queryParam.employeeId,
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
                createTimeCardEvent(request)
            }
            handler(api.getTimeCardEvent, contextRetriever) { request ->
                getTimeCardEvent(request)
            }
            handler(api.getTimeCardEvents, contextRetriever) { request ->
                getTimeCardEvents(request)
            }
        }
    }
}
