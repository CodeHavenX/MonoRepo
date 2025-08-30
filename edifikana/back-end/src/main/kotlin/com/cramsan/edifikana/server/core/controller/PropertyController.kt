package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.Routes.Property.QueryParams.PROPERTY_ID
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.CreatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdatePropertyNetworkRequest
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.PropertyService
import com.cramsan.edifikana.server.core.service.authorization.RoleBasedAccessControlService
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.HttpResponse
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
    private val rbacService: RoleBasedAccessControlService,
    private val contextRetriever: ContextRetriever,
) : Controller {

    /**
     * Handles the creation of a new property. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createProperty(call: ApplicationCall) =
        call.handleCall(
            TAG,
            "createProperty",
            contextRetriever
        ) { context ->
            if (!rbacService.hasRoleOrHigher(context, UserRole.OWNER)) {
                throw ClientRequestExceptions.UnauthorizedException(
                    "You do not have permissions to create new properties."
                )
            }
            val createPropertyRequest = call.receive<CreatePropertyNetworkRequest>()

        val newProperty = propertyService.createProperty(
            createPropertyRequest.name,
            createPropertyRequest.address,
            OrganizationId(createPropertyRequest.organizationId),
            context,
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
        checkAuthorization(context, PropertyId(propertyId), UserRole.MANAGER)

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
     * TODO: ADD A CHECK THAT THE REQUESTER IS REQUESTING ALL PROPERTIES FROM THEIR ORG
     */
    @OptIn(NetworkModel::class)
    suspend fun getProperties(call: ApplicationCall) = call.handleCall(
        TAG,
        "getProperties",
        contextRetriever,
    ) { context ->
        if (!rbacService.hasRole(context, UserRole.OWNER)
        ) {
            throw ClientRequestExceptions.UnauthorizedException(
                "You do not have permissions to see all properties in your organization."
            )
        }

        val properties = propertyService.getProperties(
            userId = context.userId,
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
        checkAuthorization(context, PropertyId(propertyId), UserRole.OWNER)

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
        checkAuthorization(context, PropertyId(propertyId), UserRole.OWNER)

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
     * Checks if the user in the [context] has at least the [requiredRole] for the [targetProperty].
     */
    private suspend fun checkAuthorization(
        context: ClientContext.AuthenticatedClientContext,
        targetProperty: PropertyId,
        requiredRole: UserRole,
    ) {
        if (!rbacService.hasRoleOrHigher(context, targetProperty, requiredRole)) {
            throw ClientRequestExceptions.UnauthorizedException(
                "You do not have permissions to edit/delete a property."
            )
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
                getProperties(call)
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
