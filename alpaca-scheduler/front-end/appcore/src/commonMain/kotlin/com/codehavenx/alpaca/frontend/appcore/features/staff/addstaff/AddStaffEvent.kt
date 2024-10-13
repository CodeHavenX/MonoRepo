package com.codehavenx.alpaca.frontend.appcore.features.staff.addstaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import kotlin.random.Random

/**
 * Events for the Add Staff screen.
 */
sealed class AddStaffEvent {

    /**
     * No operation
     */
    data object Noop : AddStaffEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : AddStaffEvent()
}
