package com.cramsan.templatereplaceme.client.lib.service

import com.cramsan.framework.annotations.FrontendService
import com.cramsan.templatereplaceme.client.lib.models.ComponentReplaceMeModel

/**
 * Client-side contract for [ComponentReplaceMe] network operations.
 *
 * Defines the operations that the front-end can perform against the backend API.
 * The implementation ([ComponentReplaceMeServiceImpl]) handles the actual HTTP calls and
 * response mapping. Business logic belongs in the Manager layer, not here.
 *
 * Registration checklist:
 * - TODO: Add `singleOf(::ComponentReplaceMeServiceImpl) { bind<ComponentReplaceMeService>() }`
 *         to ServiceModule.kt.
 *
 * TODO: Add one suspend function per backend API operation, e.g.:
 * ```
 * suspend fun getById(id: String): Result<ComponentReplaceMeModel>
 * suspend fun update(id: String, data: ComponentReplaceMeModel): Result<ComponentReplaceMeModel>
 * suspend fun delete(id: String): Result<Unit>
 * ```
 *
 * @see com.cramsan.templatereplaceme.client.lib.service.impl.ComponentReplaceMeServiceImpl
 *   for the HTTP implementation
 */
@FrontendService
interface ComponentReplaceMeService {
    /**
     * Creates a new [ComponentReplaceMe] entity via the backend API.
     *
     * @param id The identifier for the new entity.
     * @return A [Result] containing the [ComponentReplaceMeModel] or an error.
     */
    suspend fun create(id: String): Result<ComponentReplaceMeModel>
}
