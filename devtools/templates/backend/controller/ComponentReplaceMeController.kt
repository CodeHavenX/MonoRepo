package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.annotations.BackendController
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.handler
import com.cramsan.templatereplaceme.api.ComponentReplaceMeApi
import com.cramsan.templatereplaceme.lib.model.network.ComponentReplaceMeNetworkResponse
import com.cramsan.templatereplaceme.lib.model.network.CreateComponentReplaceMeNetworkRequest
import com.cramsan.templatereplaceme.server.service.ComponentReplaceMeService
import io.ktor.server.routing.Routing

/**
 * HTTP controller for [ComponentReplaceMe] operations.
 *
 * The controller layer is responsible only for:
 * - Extracting parameters from incoming HTTP requests.
 * - Delegating to [ComponentReplaceMeService] for all business logic.
 * - Converting service results to network response models.
 *
 * Rules:
 * - No business logic here — that belongs in [ComponentReplaceMeService].
 * - No data-access code here — that belongs in the datastore layer.
 * - No conditional domain decisions — only request routing and response mapping.
 *
 * Registration checklist:
 * - TODO: Add `singleOf(::ComponentReplaceMeController) { bind<Controller>() }` to
 *         ControllerModule.kt.
 * - TODO: Verify the API route is registered via `ComponentReplaceMeApi` in [registerRoutes].
 *
 * TODO: Add one suspend function per API operation, following the same
 *       extract → delegate → map pattern as [create].
 *
 * @see ComponentReplaceMeService for all business logic
 * @see ComponentReplaceMeApi for the API contract this controller implements
 */
@BackendController
class ComponentReplaceMeController(
    private val componentreplacemeService: ComponentReplaceMeService,
) : Controller {
    /**
     * Creates a new [ComponentReplaceMe] entity.
     *
     * @param request The network request containing the data for the new entity.
     * @return The [ComponentReplaceMeNetworkResponse] for the created entity.
     */
    suspend fun create(request: CreateComponentReplaceMeNetworkRequest): ComponentReplaceMeNetworkResponse {
        val result = componentreplacemeService.create(request.id)
        return result.getOrThrow().toComponentReplaceMeNetworkResponse()
    }

    override fun registerRoutes(route: Routing) {
        ComponentReplaceMeApi.register(route, Unit::class) {
            handler(api.create) { request ->
                create(request.requestBody)
            }
        }
    }
}
