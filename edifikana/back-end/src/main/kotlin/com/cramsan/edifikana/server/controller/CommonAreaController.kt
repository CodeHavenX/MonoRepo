package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.CommonAreaApi
import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.network.commonArea.CommonAreaListNetworkResponse
import com.cramsan.edifikana.lib.model.network.commonArea.CommonAreaNetworkResponse
import com.cramsan.edifikana.lib.model.network.commonArea.CreateCommonAreaNetworkRequest
import com.cramsan.edifikana.lib.model.network.commonArea.UpdateCommonAreaNetworkRequest
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.CommonAreaService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.BackendController
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.NotFoundException
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.UnauthorizedException
import io.ktor.server.routing.Routing

/**
 * Controller for common area operations within a property.
 * All operations require MANAGER role or higher.
 */
@BackendController
class CommonAreaController(private val commonAreaService: CommonAreaService, private val rbacService: RBACService) :
    Controller {
    private val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Creates a new common area. Requires MANAGER role or higher in the target property.
     */
    suspend fun createCommonArea(
        request: OperationRequest<
            CreateCommonAreaNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): CommonAreaNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.propertyId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return commonAreaService
            .createCommonArea(
                propertyId = request.requestBody.propertyId,
                name = request.requestBody.name,
                type = request.requestBody.type,
                description = request.requestBody.description,
            ).toCommonAreaNetworkResponse()
    }

    /**
     * Retrieves a single common area by its [CommonAreaId]. Requires MANAGER role or higher.
     * Returns 404 if the area does not exist or the caller is not authorized (to avoid leaking existence).
     */
    suspend fun getCommonArea(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            CommonAreaId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): CommonAreaNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw NotFoundException("Common area not found.")
        }
        return commonAreaService.getCommonArea(request.pathParam)?.toCommonAreaNetworkResponse()
            ?: throw NotFoundException("Common area not found.")
    }

    /**
     * Lists all common areas for a property. Requires MANAGER role or higher.
     */
    suspend fun getCommonAreasForProperty(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            PropertyId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): CommonAreaListNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val commonAreas =
            commonAreaService
                .getCommonAreasForProperty(request.pathParam)
                .map { it.toCommonAreaNetworkResponse() }
        return CommonAreaListNetworkResponse(commonAreas)
    }

    /**
     * Updates an existing common area. Requires MANAGER role or higher.
     */
    suspend fun updateCommonArea(
        request: OperationRequest<
            UpdateCommonAreaNetworkRequest,
            NoQueryParam,
            CommonAreaId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): CommonAreaNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return commonAreaService
            .updateCommonArea(
                commonAreaId = request.pathParam,
                name = request.requestBody.name,
                type = request.requestBody.type,
                description = request.requestBody.description,
            ).toCommonAreaNetworkResponse()
    }

    /**
     * Soft-deletes a common area. Requires MANAGER role or higher.
     */
    suspend fun deleteCommonArea(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            CommonAreaId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): NoResponseBody {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        commonAreaService.deleteCommonArea(request.pathParam)
        return NoResponseBody
    }

    /**
     * Registers all common area routes.
     */
    override fun registerRoutes(route: Routing) {
        CommonAreaApi.register(route, SupabaseContextPayload::class) {
            handler(api.createCommonArea) { request ->
                createCommonArea(request)
            }
            handler(api.getCommonArea) { request ->
                getCommonArea(request)
            }
            handler(api.getCommonAreasForProperty) { request ->
                getCommonAreasForProperty(request)
            }
            handler(api.updateCommonArea) { request ->
                updateCommonArea(request)
            }
            handler(api.deleteCommonArea) { request ->
                deleteCommonArea(request)
            }
        }
    }
}
