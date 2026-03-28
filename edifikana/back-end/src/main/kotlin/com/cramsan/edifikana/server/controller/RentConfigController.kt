package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.RentConfigApi
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.network.GetRentConfigQueryParams
import com.cramsan.edifikana.lib.model.network.RentConfigNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpsertRentConfigNetworkRequest
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.RentConfigService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
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

/**
 * Controller for rent configuration operations.
 */
@OptIn(NetworkModel::class)
class RentConfigController(
    private val rentConfigService: RentConfigService,
    private val rbacService: RBACService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Retrieves the rent configuration for the given unit. Requires MANAGER role or higher.
     */
    suspend fun getRentConfig(
        request: OperationRequest<
            NoRequestBody,
            GetRentConfigQueryParams,
            UnitId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): RentConfigNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.queryParam.orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return rentConfigService.getRentConfig(request.pathParam)?.toRentConfigNetworkResponse()
            ?: throw NotFoundException("Rent config not found for unit: ${request.pathParam}")
    }

    /**
     * Creates or updates the rent configuration for the given unit. Requires MANAGER role or higher.
     */
    suspend fun upsertRentConfig(
        request: OperationRequest<
            UpsertRentConfigNetworkRequest,
            NoQueryParam,
            UnitId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): RentConfigNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return rentConfigService.upsertRentConfig(
            unitId = request.pathParam,
            orgId = request.requestBody.orgId,
            monthlyAmount = request.requestBody.monthlyAmount,
            dueDay = request.requestBody.dueDay,
            currency = request.requestBody.currency,
            updatedBy = request.context.payload.userId,
        ).toRentConfigNetworkResponse()
    }

    /**
     * Registers all rent config routes.
     */
    override fun registerRoutes(route: Routing) {
        RentConfigApi.register(route) {
            handler(api.getRentConfig, contextRetriever) { request ->
                getRentConfig(request)
            }
            handler(api.upsertRentConfig, contextRetriever) { request ->
                upsertRentConfig(request)
            }
        }
    }
}
