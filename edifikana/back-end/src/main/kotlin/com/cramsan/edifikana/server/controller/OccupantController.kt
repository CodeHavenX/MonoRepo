package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.OccupantApi
import com.cramsan.edifikana.lib.model.network.occupant.CreateOccupantNetworkRequest
import com.cramsan.edifikana.lib.model.network.occupant.GetOccupantsForUnitQueryParams
import com.cramsan.edifikana.lib.model.network.occupant.OccupantListNetworkResponse
import com.cramsan.edifikana.lib.model.network.occupant.OccupantNetworkResponse
import com.cramsan.edifikana.lib.model.network.occupant.UpdateOccupantNetworkRequest
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.OccupantService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.NotFoundException
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.UnauthorizedException
import io.ktor.server.routing.Routing
import kotlinx.datetime.LocalDate

/**
 * Controller for occupant operations.
 *
 * Write operations (create, update, remove) require ADMIN role or higher.
 * Read operations (get, list) require EMPLOYEE role or higher.
 *
 * RBAC for create and list resolves via the unitId in the request body / query params.
 * RBAC for get, update, and remove resolves via the occupantId (occupant → unit → org lookup).
 */
@OptIn(NetworkModel::class)
class OccupantController(
    private val occupantService: OccupantService,
    private val rbacService: RBACService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Adds a new occupant to a unit. Requires ADMIN role or higher.
     */
    suspend fun createOccupant(
        request: OperationRequest<
            CreateOccupantNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): OccupantNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.unitId, UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return occupantService.addOccupant(
            unitId = request.requestBody.unitId,
            userId = request.requestBody.userId,
            addedBy = request.context.payload.userId,
            name = request.requestBody.name,
            email = request.requestBody.email,
            occupantType = request.requestBody.occupantType,
            isPrimary = request.requestBody.isPrimary,
            startDate = LocalDate.parse(request.requestBody.startDate),
            endDate = request.requestBody.endDate?.let { LocalDate.parse(it) },
        ).toOccupantNetworkResponse()
    }

    /**
     * Retrieves a single occupant. Requires EMPLOYEE role or higher, OR the caller is the occupant's linked user.
     * Returns 404 if not found or unauthorized (to avoid leaking existence).
     */
    suspend fun getOccupant(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            OccupantId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): OccupantNetworkResponse {
        val hasStaffAccess = rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.EMPLOYEE)
        if (hasStaffAccess) {
            return occupantService.getOccupant(request.pathParam)?.toOccupantNetworkResponse()
                ?: throw NotFoundException("Occupant not found.")
        }
        // Residents can read their own occupant record (where userId matches the caller).
        val occupant = occupantService.getOccupant(request.pathParam)
            ?: throw NotFoundException("Occupant not found.")
        if (occupant.userId == request.context.payload.userId) {
            return occupant.toOccupantNetworkResponse()
        }
        throw NotFoundException("Occupant not found.")
    }

    /**
     * Lists occupants for a unit. Requires EMPLOYEE role or higher.
     */
    suspend fun listOccupantsForUnit(
        request: OperationRequest<
            NoRequestBody,
            GetOccupantsForUnitQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): OccupantListNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.queryParam.unitId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val occupants = occupantService.listOccupantsForUnit(
            unitId = request.queryParam.unitId,
            includeInactive = request.queryParam.includeInactive,
        ).map { it.toOccupantNetworkResponse() }
        return OccupantListNetworkResponse(occupants)
    }

    /**
     * Updates an existing occupant. Requires ADMIN role or higher.
     */
    suspend fun updateOccupant(
        request: OperationRequest<
            UpdateOccupantNetworkRequest,
            NoQueryParam,
            OccupantId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): OccupantNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return try {
            occupantService.updateOccupant(
                occupantId = request.pathParam,
                occupantType = request.requestBody.occupantType,
                isPrimary = request.requestBody.isPrimary,
                endDate = request.requestBody.endDate?.let { LocalDate.parse(it) },
                status = request.requestBody.status,
            ).toOccupantNetworkResponse()
        } catch (e: NoSuchElementException) {
            throw NotFoundException("Occupant not found.", e)
        }
    }

    /**
     * Soft-removes an occupant. Requires ADMIN role or higher.
     * Returns 409 if the occupant is primary and other active occupants exist for the unit.
     */
    suspend fun removeOccupant(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            OccupantId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): OccupantNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return try {
            occupantService.removeOccupant(request.pathParam).toOccupantNetworkResponse()
        } catch (e: NoSuchElementException) {
            throw NotFoundException("Occupant not found.", e)
        }
    }

    /**
     * Registers all occupant routes.
     */
    override fun registerRoutes(route: Routing) {
        OccupantApi.register(route) {
            handler(api.createOccupant, contextRetriever) { request ->
                createOccupant(request)
            }
            handler(api.getOccupant, contextRetriever) { request ->
                getOccupant(request)
            }
            handler(api.listOccupantsForUnit, contextRetriever) { request ->
                listOccupantsForUnit(request)
            }
            handler(api.updateOccupant, contextRetriever) { request ->
                updateOccupant(request)
            }
            handler(api.removeOccupant, contextRetriever) { request ->
                removeOccupant(request)
            }
        }
    }
}
