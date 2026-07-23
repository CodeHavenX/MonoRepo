package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.PropertyApi
import com.cramsan.edifikana.lib.model.network.property.CreatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.network.property.GetPropertiesQueryParams
import com.cramsan.edifikana.lib.model.network.property.PropertyListNetworkResponse
import com.cramsan.edifikana.lib.model.network.property.PropertyNetworkResponse
import com.cramsan.edifikana.lib.model.network.property.UpdatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.PropertyService
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
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.UnauthorizedException
import io.ktor.server.routing.Routing

/**
 * Controller for property related operations.
 */
@BackendController
class PropertyController(private val propertyService: PropertyService, private val rbacService: RBACService) :
    Controller {
    val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Creates a new property using the provided request data.
     * Returns the created property as a network response.
     * Throws [UnauthorizedException] if the user does not have ADMIN role in the organization.
     */
    suspend fun createProperty(
        request: OperationRequest<
            CreatePropertyNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): PropertyNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.organizationId, UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val newProperty =
            propertyService
                .createProperty(
                    request.requestBody.name,
                    request.requestBody.address,
                    request.requestBody.organizationId,
                    request.requestBody.imageUrl?.url,
                    request.context,
                ).toPropertyNetworkResponse()
        return newProperty
    }

    /**
     * Retrieves a property by its [PropertyId].
     * Returns the property as a network response if the user has MANAGER role or higher.
     * Throws [UnauthorizedException] if the user does not have permission.
     */

    suspend fun getProperty(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            PropertyId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): PropertyNetworkResponse? {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return propertyService.getProperty(request.pathParam)?.toPropertyNetworkResponse()
    }

    /**
     * Retrieves the list of properties belonging to an organization.
     * Returns a list of properties as a network response.
     * Throws [UnauthorizedException] if the user does not have EMPLOYEE role or higher in the organization.
     */

    suspend fun getProperties(
        request: OperationRequest<
            NoRequestBody,
            GetPropertiesQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): PropertyListNetworkResponse {
        val organizationId = request.queryParam.organizationId
        if (!rbacService.hasRoleOrHigher(request.context, organizationId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val properties = propertyService.getProperties(organizationId).map { it.toPropertyNetworkResponse() }
        return PropertyListNetworkResponse(properties)
    }

    /**
     * Updates a property identified by [PropertyId] with the provided request data.
     * Returns the updated property as a network response.
     * Throws [UnauthorizedException] if the user does not have ADMIN role for the property.
     */

    suspend fun updateProperty(
        request: OperationRequest<
            UpdatePropertyNetworkRequest,
            NoQueryParam,
            PropertyId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): PropertyNetworkResponse {
        checkUserHasRole(request.context, request.pathParam, UserRole.ADMIN)
        val updatedProperty =
            propertyService
                .updateProperty(
                    id = request.pathParam,
                    name = request.requestBody.name,
                    address = request.requestBody.address,
                    imageUrl = request.requestBody.imageUrl?.url,
                ).toPropertyNetworkResponse()
        return updatedProperty
    }

    /**
     * Deletes a property identified by [PropertyId].
     * Returns [NoResponseBody] to indicate successful deletion.
     * Throws [UnauthorizedException] if the user does not have ADMIN role for the property.
     */
    suspend fun deleteProperty(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            PropertyId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): NoResponseBody {
        checkUserHasRole(request.context, request.pathParam, UserRole.ADMIN)
        propertyService.deleteProperty(request.pathParam)
        return NoResponseBody
    }

    /**
     * Checks if the user has the required role for the property.
     * Throws [UnauthorizedException] if the user does not have the required role.
     */
    private suspend fun checkUserHasRole(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        propId: PropertyId,
        role: UserRole,
    ) {
        if (!rbacService.hasRoleOrHigher(context, propId, role)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
    }

    /**
     * Registers the routes for the property controller.
     * Sets up the API endpoints and handlers for property operations.
     */
    override fun registerRoutes(route: Routing) {
        PropertyApi.register(route, SupabaseContextPayload::class) {
            handler(api.createProperty) { request ->
                createProperty(request)
            }
            handler(api.getProperty) { request ->
                getProperty(request)
            }
            handler(api.getProperties) { request ->
                getProperties(request)
            }
            handler(api.updateProperty) { request ->
                updateProperty(request)
            }
            handler(api.deleteProperty) { request ->
                deleteProperty(request)
            }
        }
    }
}
