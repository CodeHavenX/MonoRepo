package com.codehavenx.alpaca.frontend.appcore.features.staff.updatestaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import kotlin.random.Random

/**
 * Events for the Update Staff screen.
 */
sealed class UpdateStaffEvent {

    /**
     * No operation
     */
    data object Noop : UpdateStaffEvent()

    /**
     * Trigger an application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : UpdateStaffEvent()
}
