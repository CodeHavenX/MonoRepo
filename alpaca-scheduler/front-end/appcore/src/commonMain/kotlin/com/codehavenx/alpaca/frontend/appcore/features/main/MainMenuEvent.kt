package com.codehavenx.alpaca.frontend.appcore.features.main

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import kotlin.random.Random

/**
 * Event emitted within the main menu feature.
 */
sealed class MainMenuEvent {

    /**
     * No operation event.
     */
    data object Noop : MainMenuEvent()

    /**
     * Triggers an application event from within this feature. The [applicationEvent] is the event to trigger.
     * and the [id] is the unique identifier for this event. Generally the [id] does not need to be provided
     * as it is generated automatically.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : MainMenuEvent()
}
