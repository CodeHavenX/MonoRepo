package com.cramsan.runasimi.server.controller

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.unauthenticatedHandler
import com.cramsan.runasimi.api.RunasimiApi
import com.cramsan.runasimi.server.service.RunasimiService
import io.ktor.server.routing.Routing

/**
 * Controller exposing an endpoint to generate quechua content.
 */
@OptIn(NetworkModel::class)
class RunasimiController(
    private val runasimiService: RunasimiService,
    private val contextRetriever: ContextRetriever<Unit>,
) : Controller {

    private fun ping(): NoResponseBody {
        runasimiService.ping()
        return NoResponseBody
    }

    @OptIn(NetworkModel::class)
    override fun registerRoutes(route: Routing) {
        RunasimiApi.register(route) {
            unauthenticatedHandler(api.ping, contextRetriever) { _ ->
                ping()
            }
        }
    }
}
