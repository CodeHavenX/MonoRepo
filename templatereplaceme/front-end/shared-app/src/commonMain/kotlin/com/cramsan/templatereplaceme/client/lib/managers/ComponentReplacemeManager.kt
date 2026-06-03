package com.cramsan.templatereplaceme.client.lib.managers

import com.cramsan.framework.annotations.FrontendManager
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI
import com.cramsan.templatereplaceme.client.lib.models.ComponentReplacemeModel
import com.cramsan.templatereplaceme.client.lib.service.ComponentReplacemeService

/**
 * Coordinates [ComponentReplaceme] operations by delegating to [ComponentReplacemeService].
 *
 * The manager layer handles cross-cutting concerns (error handling, logging, caching)
 * and exposes domain-level operations to ViewModels. It must not contain business logic
 * or network details.
 */
@FrontendManager
class ComponentReplacemeManager(
    private val dependencies: ManagerDependencies,
    private val componentreplacemeService: ComponentReplacemeService,
) {
    /**
     * Creates a new [ComponentReplaceme] entity.
     *
     * @param id The identifier for the new entity.
     * @return A [Result] containing the [ComponentReplacemeModel] or an error.
     */
    suspend fun create(id: String): Result<ComponentReplacemeModel> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "Creating componentreplaceme with id=$id")
            componentreplacemeService.create(id).getOrThrow()
        }

    companion object {
        private const val TAG = "ComponentReplacemeManager"
    }
}
