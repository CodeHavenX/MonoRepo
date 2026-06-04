package com.cramsan.templatereplaceme.client.lib.managers

import com.cramsan.framework.annotations.FrontendManager
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI
import com.cramsan.templatereplaceme.client.lib.models.ComponentReplaceMeModel
import com.cramsan.templatereplaceme.client.lib.service.ComponentReplaceMeService

/**
 * Coordinates [ComponentReplaceMe] operations for ViewModels.
 *
 * The manager layer sits between ViewModels and Services. It is responsible for:
 * - Cross-cutting concerns: error handling via [getOrCatch], logging, caching.
 * - Translating service results into domain models that ViewModels understand.
 * - Hiding network/storage details from the ViewModel layer.
 *
 * Rules:
 * - No HTTP or database code here — delegate to [ComponentReplaceMeService].
 * - No UI logic here — that belongs in the ViewModel.
 * - No business logic — that belongs in the back-end Service layer.
 *
 * Registration checklist:
 * - TODO: Add `singleOf(::ComponentReplaceMeManager)` to ManagerModule.kt.
 *
 * Example usage in a ViewModel:
 * ```
 * class MyViewModel(
 *     dependencies: ViewModelDependencies,
 *     private val componentreplacemeManager: ComponentReplaceMeManager,
 * ) : BaseViewModel<...>(...) {
 *     fun loadItem(id: String) {
 *         viewModelCoroutineScope.launch {
 *             componentreplacemeManager.create(id)
 *                 .onSuccess { model -> updateUiState { it.copy(item = model) } }
 *                 .onFailure { updateUiState { it.copy(error = true) } }
 *         }
 *     }
 * }
 * ```
 *
 * TODO: Add functions for the domain operations this manager exposes to ViewModels.
 */
@FrontendManager
class ComponentReplaceMeManager(
    private val dependencies: ManagerDependencies,
    private val componentreplacemeService: ComponentReplaceMeService,
) {
    /**
     * Creates a new [ComponentReplaceMe] entity.
     *
     * @param id The identifier for the new entity.
     * @return A [Result] containing the [ComponentReplaceMeModel] or an error.
     */
    suspend fun create(id: String): Result<ComponentReplaceMeModel> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "Creating componentreplaceme with id=$id")
            componentreplacemeService.create(id).getOrThrow()
        }

    companion object {
        private const val TAG = "ComponentReplaceMeManager"
    }
}
