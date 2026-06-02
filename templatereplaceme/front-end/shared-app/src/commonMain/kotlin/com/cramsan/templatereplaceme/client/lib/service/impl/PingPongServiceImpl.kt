package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.architecture.client.service.execute
import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import com.cramsan.templatereplaceme.api.PingPonApi
import com.cramsan.templatereplaceme.client.lib.models.PongModel
import com.cramsan.templatereplaceme.client.lib.service.PingPongService
import com.cramsan.templatereplaceme.lib.model.network.PingNetworkRequest
import io.ktor.client.HttpClient

/**
 * Implementation of [PingPongService].
 */
@FrontendService
class PingPongServiceImpl(private val http: HttpClient) : PingPongService {
    override suspend fun ping(
        firstName: String,
        lastName: String,
    ): Result<PongModel> =
        runSuspendCatching(TAG) {
            val response =
                PingPonApi.ping
                    .buildRequest(
                        PingNetworkRequest(
                            firstName = firstName,
                            lastName = lastName,
                        ),
                    ).execute(http)
            val pongModel = response.toPongModel()
            pongModel
        }

    companion object {
        private const val TAG = "PingPongServiceImpl"
    }
}
