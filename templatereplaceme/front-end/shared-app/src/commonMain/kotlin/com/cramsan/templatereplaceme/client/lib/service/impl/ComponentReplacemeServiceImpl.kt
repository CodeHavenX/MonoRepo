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
 * Executes network requests via [ComponentReplacemeApi] and converts responses to
 * client domain models via the [NetworkMapper].
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
