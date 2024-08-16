package com.codehavenx.alpaca.backend.controller

import io.ktor.server.routing.Routing

@Suppress("UnnecessaryAbstractClass")
abstract class BaseController {
    abstract fun registerRoutes(route: Routing)
}
