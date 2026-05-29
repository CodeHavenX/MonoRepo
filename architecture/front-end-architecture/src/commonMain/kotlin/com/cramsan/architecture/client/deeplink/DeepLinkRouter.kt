package com.cramsan.architecture.client.deeplink

import com.cramsan.framework.annotations.FrontendManager
import com.cramsan.framework.core.compose.navigation.Destination

/**
 * Routes raw deep-link URLs or fragments to navigation [Destination]s.
 *
 * Handlers are registered by each application's DI layer via [register]. The router itself has no
 * knowledge of app-specific schemes, hosts, or destinations — keeping all routing logic in the app
 * layer and making the router reusable across projects.
 */
@FrontendManager
class DeepLinkRouter {

    private val handlers = mutableListOf<(DeepLinkParams) -> Destination?>()

    /**
     * Registers a handler. The handler receives [DeepLinkParams] and should return a [Destination]
     * if it owns this link type, or null to pass through to the next registered handler.
     */
    fun register(handler: (DeepLinkParams) -> Destination?) {
        handlers.add(handler)
    }

    /**
     * Resolves [rawInput] to the first matching [Destination], or null if no handler claims it.
     */
    fun resolve(rawInput: String): Destination? {
        val params = DeepLinkParams.parse(rawInput)
        return handlers.firstNotNullOfOrNull { it(params) }
    }
}
