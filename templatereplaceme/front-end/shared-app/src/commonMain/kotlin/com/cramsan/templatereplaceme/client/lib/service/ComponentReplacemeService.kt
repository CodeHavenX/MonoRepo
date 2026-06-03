package com.cramsan.templatereplaceme.client.lib.service

import com.cramsan.framework.annotations.FrontendService
import com.cramsan.templatereplaceme.client.lib.models.ComponentReplacemeModel

/**
 * Client-side contract for [ComponentReplaceme] network operations.
 *
 * Implementations call the backend API and convert responses to client domain models.
 * Business logic belongs in the Manager layer, not here.
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
