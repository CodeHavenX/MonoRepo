package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.architecture.client.service.execute
import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import com.cramsan.templatereplaceme.api.ComponentReplaceMeApi
import com.cramsan.templatereplaceme.client.lib.models.ComponentReplaceMeModel
import com.cramsan.templatereplaceme.client.lib.service.ComponentReplaceMeService
import com.cramsan.templatereplaceme.lib.model.network.CreateComponentReplaceMeNetworkRequest
import io.ktor.client.HttpClient

/**
 * HTTP implementation of [ComponentReplaceMeService].
 *
 * Executes network requests against the backend via [ComponentReplaceMeApi] and converts
 * the raw [ComponentReplaceMeNetworkResponse] into the client domain model
 * [ComponentReplaceMeModel] via the network mapper.
 *
 * Rules:
 * - No business logic here — that belongs in [ComponentReplaceMeManager].
 * - All network calls must go through [ComponentReplaceMeApi] — never use raw URLs.
 * - Wrap results in [runSuspendCatching] so callers receive a [Result] instead of exceptions.
 *
 * TODO: Add one override per function declared in [ComponentReplaceMeService].
 *       Each implementation should follow the same pattern:
 *       1. Build the request with the appropriate API operation.
 *       2. Execute it via `execute(http)`.
 *       3. Map the response to the client model.
 */
@FrontendService
class ComponentReplaceMeServiceImpl(private val http: HttpClient) : ComponentReplaceMeService {
    /**
     * Creates a new [ComponentReplaceMe] entity by calling the backend API.
     */
    override suspend fun create(id: String): Result<ComponentReplaceMeModel> =
        runSuspendCatching(TAG) {
            val response =
                ComponentReplaceMeApi.create
                    .buildRequest(CreateComponentReplaceMeNetworkRequest(id = id))
                    .execute(http)
            response.toComponentReplaceMeModel()
        }

    companion object {
        private const val TAG = "ComponentReplaceMeServiceImpl"
    }
}
