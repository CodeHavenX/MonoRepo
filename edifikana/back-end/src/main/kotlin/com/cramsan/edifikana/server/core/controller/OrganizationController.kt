package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.Routes.Organization.QueryParams.ORGANIZATION_ID
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.OrganizationService
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.HttpResponse
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
) : Controller {

    /**
     * Handles the retrieval of an organization. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getOrganization(call: ApplicationCall) = call.handleCall(
        TAG,
        "getOrganization",
        contextRetriever,
    ) { context ->
        val orgId = requireNotNull(call.parameters[ORGANIZATION_ID])
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

    override fun registerRoutes(route: Routing) {
        route.route(Routes.Organization.PATH) {
            get("{$ORGANIZATION_ID}") {
                getOrganization(call)
            }
        }
    }

    companion object {
        private const val TAG = "OrganizationController"
    }
}
