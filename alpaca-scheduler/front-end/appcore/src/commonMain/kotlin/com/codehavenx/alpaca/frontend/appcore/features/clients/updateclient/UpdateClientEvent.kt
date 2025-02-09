package com.codehavenx.alpaca.frontend.appcore.features.clients.updateclient

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Events for the Update Client screen.
 */
sealed class UpdateClientEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : UpdateClientEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : UpdateClientEvent()
}
