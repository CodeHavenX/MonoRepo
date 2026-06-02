package com.cramsan.templatereplaceme.api

import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.networkapi.Api
import com.cramsan.templatereplaceme.lib.model.network.PingNetworkRequest
import com.cramsan.templatereplaceme.lib.model.network.PongNetworkResponse
import io.ktor.http.HttpMethod

/**
 * Singleton object representing the PingPong API.
 */
object PingPongApi : Api("ping") {
    /**
     * HTTP POST operation at route `"ping"`.
     * Accepts a [PingNetworkRequest] body and returns a [PongNetworkResponse].
     */
    val ping =
        operation<
            PingNetworkRequest,
            NoQueryParam,
            NoPathParam,
            PongNetworkResponse,
            >(HttpMethod.Post)
}
