package com.cramsan.edifikana.client.lib.features.management.property

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Property feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class PropertyEvent : ViewModelEvent {

    /**
     * Event to show the dialog for removing a property.
     */
    data object ShowRemoveDialog : PropertyEvent()

    /**
     * Event to show a dialog prompting the user to save changes before exiting.
     */
    data object ShowSaveBeforeExitingDialog : PropertyEvent()
}
