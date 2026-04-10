package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.UnitApi
import com.cramsan.edifikana.lib.model.network.GetUnitsQueryParams
import com.cramsan.edifikana.lib.model.network.unit.CreateUnitNetworkRequest
import com.cramsan.edifikana.lib.model.network.unit.UnitListNetworkResponse
import com.cramsan.edifikana.lib.model.network.unit.UnitNetworkResponse
import com.cramsan.edifikana.lib.model.network.unit.UpdateUnitNetworkRequest
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.UnitService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.NotFoundException
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.UnauthorizedException
import io.ktor.server.routing.Routing

/**
 * Controller for unit CRUD operations.
 */
@OptIn(NetworkModel::class)
class UnitController(
    private val unitService: UnitService,
    private val rbacService: RBACService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Creates a new unit. Requires MANAGER role or higher in the target org.
     */
    suspend fun createUnit(
        request: OperationRequest<
            CreateUnitNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): UnitNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.propertyId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return unitService.createUnit(
            propertyId = request.requestBody.propertyId,
            unitNumber = request.requestBody.unitNumber,
            bedrooms = request.requestBody.bedrooms,
            bathrooms = request.requestBody.bathrooms,
            sqFt = request.requestBody.sqFt,
            floor = request.requestBody.floor,
            notes = request.requestBody.notes,
        ).toUnitNetworkResponse()
    }

    /**
     * Retrieves a single unit by its [UnitId]. Requires EMPLOYEE role or higher.
     */
    suspend fun getUnit(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            UnitId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): UnitNetworkResponse {
        val unitId = request.pathParam
        if (!rbacService.hasRoleOrHigher(request.context, unitId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return unitService.getUnit(unitId)?.toUnitNetworkResponse()
            ?: throw NotFoundException("Unit not found: $unitId")
    }

    /**
     * Lists all units for the org in the query params. Requires EMPLOYEE role or higher.
     */
    suspend fun getUnits(
        request: OperationRequest<
            NoRequestBody,
            GetUnitsQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): UnitListNetworkResponse {
        val propertyId = request.queryParam.propertyId
        if (!rbacService.hasRoleOrHigher(request.context, propertyId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val units = unitService.getUnits(
            propertyId = propertyId,
        ).map { it.toUnitNetworkResponse() }
        return UnitListNetworkResponse(units)
    }

    /**
     * Updates a unit's fields. Requires MANAGER role or higher.
     */
    suspend fun updateUnit(
        request: OperationRequest<
            UpdateUnitNetworkRequest,
            NoQueryParam,
            UnitId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): UnitNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return unitService.updateUnit(
            unitId = request.pathParam,
            unitNumber = request.requestBody.unitNumber,
            bedrooms = request.requestBody.bedrooms,
            bathrooms = request.requestBody.bathrooms,
            sqFt = request.requestBody.sqFt,
            floor = request.requestBody.floor,
            notes = request.requestBody.notes,
        ).toUnitNetworkResponse()
    }

    /**
     * Soft-deletes a unit. Requires ADMIN role or higher.
     */
    suspend fun deleteUnit(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            UnitId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): NoResponseBody {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        unitService.deleteUnit(request.pathParam)
        return NoResponseBody
    }

    /**
     * Registers all unit routes.
     */
    override fun registerRoutes(route: Routing) {
        UnitApi.register(route) {
            handler(api.createUnit, contextRetriever) { request ->
                createUnit(request)
            }
            handler(api.getUnit, contextRetriever) { request ->
                getUnit(request)
            }
            handler(api.getUnits, contextRetriever) { request ->
                getUnits(request)
            }
            handler(api.updateUnit, contextRetriever) { request ->
                updateUnit(request)
            }
            handler(api.deleteUnit, contextRetriever) { request ->
                deleteUnit(request)
            }
        }
    }
}
