package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.CommonAreaApi
import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.CommonAreaListNetworkResponse
import com.cramsan.edifikana.lib.model.network.CommonAreaNetworkResponse
import com.cramsan.edifikana.lib.model.network.CreateCommonAreaNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateCommonAreaNetworkRequest
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.CommonAreaService
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
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.UnauthorizedException
import io.ktor.server.routing.Routing

/**
 * Controller for common area operations within a property.
 * All operations require MANAGER role or higher.
 */
@OptIn(NetworkModel::class)
class CommonAreaController(
    private val commonAreaService: CommonAreaService,
    private val rbacService: RBACService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Creates a new common area. Requires MANAGER role or higher in the target property.
     */
    suspend fun createCommonArea(
        request: OperationRequest<
            CreateCommonAreaNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): CommonAreaNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.propertyId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return commonAreaService.createCommonArea(
            orgId = request.requestBody.orgId,
            propertyId = request.requestBody.propertyId,
            name = request.requestBody.name,
            type = request.requestBody.type,
            description = request.requestBody.description,
        ).toCommonAreaNetworkResponse()
    }

    /**
     * Retrieves a single common area by its [CommonAreaId]. Requires MANAGER role or higher.
     */
    suspend fun getCommonArea(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            CommonAreaId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): CommonAreaNetworkResponse? {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            return null
        }
        return commonAreaService.getCommonArea(request.pathParam)?.toCommonAreaNetworkResponse()
    }

    /**
     * Lists all common areas for a property. Requires MANAGER role or higher.
     */
    suspend fun getCommonAreasForProperty(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            PropertyId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): CommonAreaListNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val commonAreas = commonAreaService.getCommonAreasForProperty(request.pathParam)
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
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): CommonAreaNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return commonAreaService.updateCommonArea(
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
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
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
        CommonAreaApi.register(route) {
            handler(api.createCommonArea, contextRetriever) { request ->
                createCommonArea(request)
            }
            handler(api.getCommonArea, contextRetriever) { request ->
                getCommonArea(request)
            }
            handler(api.getCommonAreasForProperty, contextRetriever) { request ->
                getCommonAreasForProperty(request)
            }
            handler(api.updateCommonArea, contextRetriever) { request ->
                updateCommonArea(request)
            }
            handler(api.deleteCommonArea, contextRetriever) { request ->
                deleteCommonArea(request)
            }
        }
    }
}
