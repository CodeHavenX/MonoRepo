package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.Routes.Organization.QueryParams.ORGANIZATION_ID
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.OrganizationService
import com.cramsan.edifikana.server.core.service.authorization.RBACService
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.HttpResponse
import com.cramsan.framework.utils.exceptions.UnauthorizedException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route

/**
 * Controller for organization related operations. CRUD operations for organizations.
 */
class OrganizationController(
    private val organizationService: OrganizationService,
    private val contextRetriever: ContextRetriever,
    private val rbacService: RBACService,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action."

    /**
     * Handles the retrieval of an organization. The [call] parameter is the request context.
     * Only users with required role or higher can retrieve the organization data
     */
    @OptIn(NetworkModel::class)
    suspend fun getOrganization(call: ApplicationCall) = call.handleCall(
        TAG,
        "getOrganization",
        contextRetriever,
    ) { context ->
        val orgId = requireNotNull(call.parameters[ORGANIZATION_ID])
        if (!rbacService.hasRoleOrHigher(context, OrganizationId(orgId), UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val org = organizationService.getOrganization(OrganizationId(orgId))?.toOrganizationNetworkResponse()

        val statusCode = if (org == null) {
            HttpStatusCode.NotFound
        } else {
            HttpStatusCode.OK
        }
        HttpResponse(
            status = statusCode,
            body = org,
        )
    }

    /**
     * Handles the retrieval of the list of organizations that you belong to.
     * All users can retrieve a list of organizations they belong to.
     */
    @OptIn(NetworkModel::class)
    suspend fun getOrganizationList(call: ApplicationCall) = call.handleCall(
        TAG,
        "getOrganizationList",
        contextRetriever,
    ) { context ->
        val orgs = organizationService.getOrganizations(context.userId).map { it.toOrganizationNetworkResponse() }

        HttpResponse(
            status = HttpStatusCode.OK,
            body = orgs,
        )
    }

    override fun registerRoutes(route: Routing) {
        route.route(Routes.Organization.PATH) {
            get {
                getOrganizationList(call)
            }
            get("{$ORGANIZATION_ID}") {
                getOrganization(call)
            }
        }
    }

    companion object {
        private const val TAG = "OrganizationController"
    }
}
