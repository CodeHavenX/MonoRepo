package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.PROPERTY_ID
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.CreatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdatePropertyNetworkRequest
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.PropertyService
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
    private val contextRetriever: ContextRetriever,
) {

    /**
     * Handles the creation of a new property. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createProperty(call: ApplicationCall) = call.handleCall(TAG, "createProperty", contextRetriever) {
        val createPropertyRequest = call.receive<CreatePropertyNetworkRequest>()

        val newProperty = propertyService.createProperty(
            createPropertyRequest.name,
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
    suspend fun getProperty(call: ApplicationCall) = call.handleCall(TAG, "getProperty", contextRetriever) {
        val propertyId = requireNotNull(call.parameters[PROPERTY_ID])

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
    suspend fun getProperties(call: ApplicationCall) = call.handleCall(
        TAG,
        "getProperties",
        contextRetriever,
    ) { context ->
        val userId = getAuthenticatedClientContext(context).userId

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
    suspend fun updateProperty(call: ApplicationCall) = call.handleCall(TAG, "updateProperty", contextRetriever) {
        val propertyId = requireNotNull(call.parameters[PROPERTY_ID])

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
    suspend fun deleteProperty(call: RoutingCall) = call.handleCall(TAG, "deleteProperty", contextRetriever) {
        val propertyId = requireNotNull(call.parameters[PROPERTY_ID])

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

    companion object {
        private const val TAG = "PropertyController"

        /**
         * Registers the routes for the property controller.
         */
        fun PropertyController.registerRoutes(route: Routing) {
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
    }
}
