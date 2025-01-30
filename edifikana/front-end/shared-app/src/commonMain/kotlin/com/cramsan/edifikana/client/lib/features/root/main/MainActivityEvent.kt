package com.cramsan.edifikana.client.lib.features.root.main

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import kotlin.random.Random

/**
 * Events that can be triggered in the main activity.
 */
sealed class MainActivityEvent {

    /**
     * No operation.
     */
    data object Noop : MainActivityEvent()

    /**
     * Trigger application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    /**
     * Navigate to a destination.
     */
    data class Navigate(
        val destination: MainRouteDestination,
        val popToRoot: Boolean = false,
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()

    /**
     * Navigate back.
     */
    data class NavigateBack(
        val id: Int = Random.nextInt(),
    ) : MainActivityEvent()
}
