package com.cramsan.edifikana.client.lib.features.root.auth

import kotlin.random.Random

/**
 * Events that can be triggered in the main activity.
 */
sealed class AuthActivityEvent {

    /**
     * No operation.
     */
    data object Noop : AuthActivityEvent()

    /**
     * Navigate to a route.
     */
    data class Navigate(
        val destination: AuthRouteDestination,
        val id: Int = Random.nextInt(),
    ) : AuthActivityEvent()
}
