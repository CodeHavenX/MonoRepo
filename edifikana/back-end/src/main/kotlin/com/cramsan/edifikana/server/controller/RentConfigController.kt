package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.RentConfigApi
import com.cramsan.edifikana.lib.model.network.rent.RentConfigNetworkRequest
import com.cramsan.edifikana.lib.model.network.rent.RentConfigNetworkResponse
import com.cramsan.edifikana.lib.model.unit.UnitId
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
 *
 * Write operations (set) require ADMIN role or higher.
 * Read operations (get) require EMPLOYEE role or higher.
 *
 * RBAC resolves via unitId → unit.orgId.
 *
 * TODO: Resident read-only access via unit_occupants is deferred to a future phase.
 */
@OptIn(NetworkModel::class)
class RentConfigController(
    private val rentConfigService: RentConfigService,
    private val rbacService: RBACService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Retrieves the rent configuration for a unit. Requires EMPLOYEE role or higher.
     * Returns 404 if no rent config exists or the caller is not authorized.
     */
    suspend fun getRentConfig(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            UnitId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): RentConfigNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.EMPLOYEE)) {
            throw NotFoundException("Rent configuration not found.")
        }
        return rentConfigService.getRentConfig(request.pathParam)?.toRentConfigNetworkResponse()
            ?: throw NotFoundException("Rent configuration not found.")
    }

    /**
     * Creates or updates the rent configuration for a unit. Requires ADMIN role or higher.
     */
    suspend fun setRentConfig(
        request: OperationRequest<
            RentConfigNetworkRequest,
            NoQueryParam,
            UnitId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): RentConfigNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return rentConfigService.setRentConfig(
            unitId = request.pathParam,
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
            handler(api.setRentConfig, contextRetriever) { request ->
                setRentConfig(request)
            }
        }
    }
}
