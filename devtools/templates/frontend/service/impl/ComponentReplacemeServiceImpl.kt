package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.architecture.client.service.execute
import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import com.cramsan.templatereplaceme.api.ComponentReplacemeApi
import com.cramsan.templatereplaceme.client.lib.models.ComponentReplacemeModel
import com.cramsan.templatereplaceme.client.lib.service.ComponentReplacemeService
import com.cramsan.templatereplaceme.lib.model.network.CreateComponentReplacemeNetworkRequest
import io.ktor.client.HttpClient

/**
 * HTTP implementation of [ComponentReplacemeService].
 *
 * Executes network requests against the backend via [ComponentReplacemeApi] and converts
 * the raw [ComponentReplacemeNetworkResponse] into the client domain model
 * [ComponentReplacemeModel] via the network mapper.
 *
 * Rules:
 * - No business logic here — that belongs in [ComponentReplacemeManager].
 * - All network calls must go through [ComponentReplacemeApi] — never use raw URLs.
 * - Wrap results in [runSuspendCatching] so callers receive a [Result] instead of exceptions.
 *
 * TODO: Add one override per function declared in [ComponentReplacemeService].
 *       Each implementation should follow the same pattern:
 *       1. Build the request with the appropriate API operation.
 *       2. Execute it via `execute(http)`.
 *       3. Map the response to the client model.
 */
@FrontendService
class ComponentReplacemeServiceImpl(private val http: HttpClient) : ComponentReplacemeService {
    /**
     * Creates a new [ComponentReplaceme] entity by calling the backend API.
     */
    override suspend fun create(id: String): Result<ComponentReplacemeModel> =
        runSuspendCatching(TAG) {
            val response =
                ComponentReplacemeApi.create
                    .buildRequest(CreateComponentReplacemeNetworkRequest(id = id))
                    .execute(http)
            response.toComponentReplacemeModel()
        }

    companion object {
        private const val TAG = "ComponentReplacemeServiceImpl"
    }
}
