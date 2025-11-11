package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.OrganizationApi
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.network.OrganizationNetworkListNetworkResponse
import com.cramsan.edifikana.lib.model.network.OrganizationNetworkResponse
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.OrganizationService
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
import com.cramsan.framework.utils.exceptions.UnauthorizedException
import io.ktor.server.routing.Routing

/**
 * Controller for organization related operations. CRUD operations for organizations.
 */
@OptIn(NetworkModel::class)
class OrganizationController(
    private val organizationService: OrganizationService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
    private val rbacService: RBACService,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action."

    /**
     * Retrieves an organization by its [OrganizationId].
     * Returns the organization as a network response if the authenticated context has the required role.
     * Throws [UnauthorizedException] if the user does not have permission.
     */
    suspend fun getOrganization(
        request:
        OperationRequest<
            NoRequestBody,
            NoQueryParam,
            OrganizationId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): OrganizationNetworkResponse? {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val org = organizationService.getOrganization(request.pathParam)?.toOrganizationNetworkResponse()
        return org
    }

    /**
     * Retrieves the list of organizations for the authenticated user.
     * Returns a list of organizations as a network response.
     */
    suspend fun getOrganizationList(
        request:
        OperationRequest<
            NoRequestBody,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): OrganizationNetworkListNetworkResponse {
        val orgs = organizationService.getOrganizations(request.context.payload.userId).map {
            it.toOrganizationNetworkResponse()
        }
        return OrganizationNetworkListNetworkResponse(orgs)
    }

    /**
     * Registers the routes for the organization controller.
     * Sets up the API endpoints and handlers for organization operations.
     */
    override fun registerRoutes(route: Routing) {
        OrganizationApi.register(route) {
            handler(api.getOrganizationList, contextRetriever) { request ->
                getOrganizationList(request)
            }
            handler(api.getOrganization, contextRetriever) { request ->
                getOrganization(request)
            }
        }
    }
}
