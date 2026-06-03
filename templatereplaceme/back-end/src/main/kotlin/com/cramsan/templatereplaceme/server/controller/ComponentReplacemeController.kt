package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.annotations.BackendController
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.unauthenticatedHandler
import com.cramsan.templatereplaceme.api.ComponentReplacemeApi
import com.cramsan.templatereplaceme.lib.model.network.ComponentReplacemeNetworkResponse
import com.cramsan.templatereplaceme.lib.model.network.CreateComponentReplacemeNetworkRequest
import com.cramsan.templatereplaceme.server.service.ComponentReplacemeService
import io.ktor.server.routing.Routing

/**
 * Validates and routes HTTP requests for [ComponentReplaceme] operations.
 *
 * The controller layer is responsible only for extracting request parameters and
 * delegating to the service layer. No business logic, data transformations, or
 * conditional domain decisions belong here.
 *
 * @see ComponentReplacemeService for all business logic
 * @see ComponentReplacemeApi for the API contract this controller implements
 */
@BackendController
class ComponentReplacemeController(
    private val componentreplacemeService: ComponentReplacemeService,
    private val contextRetriever: ContextRetriever<Unit>,
) : Controller {
    /**
     * Creates a new [ComponentReplaceme] entity.
     *
     * @param request The network request containing the data for the new entity.
     * @return The [ComponentReplacemeNetworkResponse] for the created entity.
     */
    suspend fun create(request: CreateComponentReplacemeNetworkRequest): ComponentReplacemeNetworkResponse {
        val result = componentreplacemeService.create(request.id)
        return result.getOrThrow().toComponentReplacemeNetworkResponse()
    }

    override fun registerRoutes(route: Routing) {
        ComponentReplacemeApi.register(route) {
            unauthenticatedHandler(api.create, contextRetriever) { request ->
                create(request.requestBody)
            }
        }
    }
}
