package com.codehavenx.alpaca.frontend.appcore.features.staff.liststaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Events for the List Staff screen.
 */
sealed class ListStaffsEvent : ViewModelEvent {

    /**
     * No operation
     */
    data object Noop : ListStaffsEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : ListStaffsEvent()
}
