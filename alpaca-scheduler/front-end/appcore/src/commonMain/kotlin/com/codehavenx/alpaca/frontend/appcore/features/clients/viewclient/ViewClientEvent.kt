package com.codehavenx.alpaca.frontend.appcore.features.clients.viewclient

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import kotlin.random.Random

/**
 * Events for the View Client screen.
 */
sealed class ViewClientEvent {

    /**
     * No operation
     */
    data object Noop : ViewClientEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : ViewClientEvent()
}
