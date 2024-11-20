package com.codehavenx.alpaca.frontend.appcore.features.clients.addclient

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import kotlin.random.Random

/**
 * Events for the Add Client screen.
 */
sealed class AddClientEvent {

    /**
     * No operation
     */
    data object Noop : AddClientEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : AddClientEvent()
}
