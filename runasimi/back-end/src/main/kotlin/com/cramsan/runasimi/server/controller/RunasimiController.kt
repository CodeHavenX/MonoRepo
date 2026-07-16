package com.cramsan.runasimi.server.controller

import com.cramsan.framework.annotations.BackendController
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.handler
import com.cramsan.runasimi.api.RunasimiApi
import com.cramsan.runasimi.server.service.RunasimiService
import io.ktor.server.routing.Routing

/**
 * Controller exposing an endpoint to generate quechua content.
 */
@BackendController
class RunasimiController(private val runasimiService: RunasimiService) : Controller {
    private fun ping(): NoResponseBody {
        runasimiService.ping()
        return NoResponseBody
    }

    override fun registerRoutes(route: Routing) {
        RunasimiApi.register(route, Unit::class) {
            handler(api.ping) { _ ->
                ping()
            }
        }
    }
}
