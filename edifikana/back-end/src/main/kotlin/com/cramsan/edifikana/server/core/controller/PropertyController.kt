package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.Routes.Property.QueryParams.PROPERTY_ID
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.CreatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdatePropertyNetworkRequest
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.PropertyService
import com.cramsan.edifikana.server.core.service.authorization.RBACService
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.HttpResponse
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.UnauthorizedException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

/**
 * Controller for property related operations.
 */
class PropertyController(
    private val propertyService: PropertyService,
    private val contextRetriever: ContextRetriever,
    private val rbacService: RBACService,
) : Controller {


    val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Handles the creation of a new property. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createProperty(call: ApplicationCall) = call.handleCall(
        TAG, "createProperty",
        contextRetriever
    ) { context ->
        val createPropertyRequest = call.receive<CreatePropertyNetworkRequest>()
        val authenticatedContext = requireAuthenticatedClientContext(context)
        // check user has perms in their org to create new properties
        if (!rbacService.hasRole(context, createPropertyRequest.organizationId, UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val newProperty = propertyService.createProperty(
            createPropertyRequest.name,
            createPropertyRequest.address,
            createPropertyRequest.organizationId,
            authenticatedContext,
        ).toPropertyNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = newProperty,
        )
    }

    /**
     * Handles the retrieval of a property. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getProperty(call: ApplicationCall) = call.handleCall(
        TAG,
        "getProperty",
        contextRetriever
    ) { context ->
        val propertyId = requireNotNull(call.parameters[PROPERTY_ID])
        if (!rbacService.hasRoleOrHigher(context, PropertyId(propertyId), UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val property = propertyService.getProperty(
            PropertyId(propertyId),
        )?.toPropertyNetworkResponse()

        val statusCode = if (property == null) {
            HttpStatusCode.NotFound
        } else {
            HttpStatusCode.OK
        }

        HttpResponse(
            status = statusCode,
            body = property,
        )
    }

    /**
     * Handles the retrieval of a list of properties. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getAssignedProperties(call: ApplicationCall) = call.handleCall(
        TAG,
        "getAssignedProperties",
        contextRetriever,
    ) { context ->
        val userId = requireAuthenticatedClientContext(context).userId
        // TODO: UPDATE THIS METHOD TO PASS THE ORG ID TO ENSURE USERS ONLY GET PROPERTIES THEY ARE ASSIGNED TO
        val properties = propertyService.getProperties(
            userId = userId,
        ).map { it.toPropertyNetworkResponse() }

        HttpResponse(
            status = HttpStatusCode.OK,
            body = properties,
        )
    }

    /**
     * Handles the update of a property. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateProperty(call: ApplicationCall) = call.handleCall(
        TAG,
        "updateProperty",
        contextRetriever
    ) { context ->
        val propertyId = requireNotNull(call.parameters[PROPERTY_ID])
        checkUserHasRole(context, PropertyId(propertyId), UserRole.ADMIN)
        val updatePropertyRequest = call.receive<UpdatePropertyNetworkRequest>()

        val updatedProperty = propertyService.updateProperty(
            id = PropertyId(propertyId),
            name = updatePropertyRequest.name,
        ).toPropertyNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = updatedProperty,
        )
    }

    /**
     * Handles the deletion of a property. The [call] parameter is the request context.
     */
    suspend fun deleteProperty(call: RoutingCall) = call.handleCall(
        TAG,
        "deleteProperty",
        contextRetriever
    ) { context ->
        val propertyId = requireNotNull(call.parameters[PROPERTY_ID])
        checkUserHasRole(context, PropertyId(propertyId), UserRole.ADMIN)
        val success = propertyService.deleteProperty(
            PropertyId(propertyId),
        )

        val statusCode = if (success) {
            HttpStatusCode.OK
        } else {
            HttpStatusCode.NotFound
        }

        HttpResponse(
            status = statusCode,
            body = null,
        )
    }

    /**
     * Checks if the user has the required authorization permission based on role in their org for the given property.
     *
     * @param context The authenticated client context.
     * @param propId The ID of the property to check against.
     * @param role The required user role for authorization.
     * @throws UnauthorizedException if the user does not have the required role.
     */
    private suspend fun checkUserHasRole(
        context: ClientContext.AuthenticatedClientContext,
        propId: PropertyId,
        role: UserRole
    ) {
        if (!rbacService.hasRole(context, propId, role)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
    }

    /**
     * Registers the routes for the property controller.
     */
    override fun registerRoutes(route: Routing) {
        route.route(Routes.Property.PATH) {
            post {
                createProperty(call)
            }
            get("{$PROPERTY_ID}") {
                getProperty(call)
            }
            get {
                getAssignedProperties(call)
            }
            put("{$PROPERTY_ID}") {
                updateProperty(call)
            }
            delete("{$PROPERTY_ID}") {
                deleteProperty(call)
            }
        }
    }

    companion object {
        private const val TAG = "PropertyController"
    }
}
