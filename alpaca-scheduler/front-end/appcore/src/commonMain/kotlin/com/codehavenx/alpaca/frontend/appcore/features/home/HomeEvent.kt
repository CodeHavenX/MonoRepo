package com.codehavenx.alpaca.frontend.appcore.features.home

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * The events for the home feature.
 */
sealed class HomeEvent : ViewModelEvent {

    /**
     * A no-op event.
     */
    data object Noop : HomeEvent()

    /**
     * A trigger application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : HomeEvent()
}
