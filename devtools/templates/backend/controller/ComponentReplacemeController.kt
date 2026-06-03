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
 * HTTP controller for [ComponentReplaceme] operations.
 *
 * The controller layer is responsible only for:
 * - Extracting parameters from incoming HTTP requests.
 * - Delegating to [ComponentReplacemeService] for all business logic.
 * - Converting service results to network response models.
 *
 * Rules:
 * - No business logic here — that belongs in [ComponentReplacemeService].
 * - No data-access code here — that belongs in the datastore layer.
 * - No conditional domain decisions — only request routing and response mapping.
 *
 * Registration checklist:
 * - TODO: Add `singleOf(::ComponentReplacemeController) { bind<Controller>() }` to
 *         ControllerModule.kt.
 * - TODO: Verify the API route is registered via `ComponentReplacemeApi` in [registerRoutes].
 *
 * TODO: Add one suspend function per API operation, following the same
 *       extract → delegate → map pattern as [create].
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
