package com.cramsan.templatereplaceme.client.lib.service

import com.cramsan.framework.annotations.FrontendService
import com.cramsan.templatereplaceme.client.lib.models.ComponentReplacemeModel

/**
 * Client-side contract for [ComponentReplaceme] network operations.
 *
 * Defines the operations that the front-end can perform against the backend API.
 * The implementation ([ComponentReplacemeServiceImpl]) handles the actual HTTP calls and
 * response mapping. Business logic belongs in the Manager layer, not here.
 *
 * Registration checklist:
 * - TODO: Add `singleOf(::ComponentReplacemeServiceImpl) { bind<ComponentReplacemeService>() }`
 *         to ServiceModule.kt.
 *
 * TODO: Add one suspend function per backend API operation, e.g.:
 * ```
 * suspend fun getById(id: String): Result<ComponentReplacemeModel>
 * suspend fun update(id: String, data: ComponentReplacemeModel): Result<ComponentReplacemeModel>
 * suspend fun delete(id: String): Result<Unit>
 * ```
 *
 * @see com.cramsan.templatereplaceme.client.lib.service.impl.ComponentReplacemeServiceImpl
 *   for the HTTP implementation
 */
@FrontendService
interface ComponentReplacemeService {
    /**
     * Creates a new [ComponentReplaceme] entity via the backend API.
     *
     * @param id The identifier for the new entity.
     * @return A [Result] containing the [ComponentReplacemeModel] or an error.
     */
    suspend fun create(id: String): Result<ComponentReplacemeModel>
}
