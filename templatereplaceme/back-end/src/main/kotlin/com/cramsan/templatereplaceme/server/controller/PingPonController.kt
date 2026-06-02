package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.annotations.BackendController
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.unauthenticatedHandler
import com.cramsan.templatereplaceme.api.PingPonApi
import com.cramsan.templatereplaceme.lib.model.network.PingNetworkRequest
import com.cramsan.templatereplaceme.lib.model.network.PongNetworkResponse
import com.cramsan.templatereplaceme.server.service.PingPongService
import io.ktor.server.routing.Routing

/**
 * Controller for PingPong related operations.
 */
@BackendController
class PingPonController(private val pingPongService: PingPongService, private val contextRetriever: ContextRetriever<Unit>) :
    Controller {
    /**
     * Make a PingPong call.
     *
     * @param pingNetworkRequest The request containing Ping details.
     * @return The created Pong network response.
     */

    suspend fun ping(pingNetworkRequest: PingNetworkRequest): PongNetworkResponse {
        val pong =
            pingPongService.ping(
                firstName = pingNetworkRequest.firstName,
                lastName = pingNetworkRequest.lastName,
            )

        return pong.getOrThrow().toPongNetworkResponse()
    }

    override fun registerRoutes(route: Routing) {
        PingPonApi.register(route) {
            unauthenticatedHandler(api.ping, contextRetriever) { request ->
                ping(request.requestBody)
            }
        }
    }
}
