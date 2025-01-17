package com.cramsan.edifikana.client.lib.features.root.admin.properties

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.root.admin.AdminActivityEvent
import kotlin.random.Random

/**
 * Events that can be triggered within the domain of the PropertyManager feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class PropertyManagerEvent {

    /**
     * No operation.
     */
    data object Noop : PropertyManagerEvent()

    /**
     * Trigger application event. This event is sent to the application's view model to be handled.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: EdifikanaApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : PropertyManagerEvent()

    /**
     * Trigger activity event. This event is triggered within a feature ViewModel and it will be
     * consumed by the activity that is hosting this feature.
     *
     * This event is optional, it can be removed if not needed.
     */
    data class TriggerActivityEvent(
        val activityEvent: AdminActivityEvent,
        val id: Int = Random.nextInt(),
    ) : PropertyManagerEvent()
}
