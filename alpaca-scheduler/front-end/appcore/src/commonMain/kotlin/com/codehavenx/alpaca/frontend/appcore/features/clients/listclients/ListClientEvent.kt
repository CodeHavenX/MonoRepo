package com.codehavenx.alpaca.frontend.appcore.features.clients.listclients

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import kotlin.random.Random

/**
 * Events for the List Clients screen.
 */
sealed class ListClientEvent {

    /**
     * No operation
     */
    data object Noop : ListClientEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : ListClientEvent()
}
