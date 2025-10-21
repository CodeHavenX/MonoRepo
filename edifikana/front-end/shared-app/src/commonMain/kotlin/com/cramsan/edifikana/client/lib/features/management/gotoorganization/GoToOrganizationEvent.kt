package com.cramsan.edifikana.client.lib.features.management.gotoorganization

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the GoToOrganization feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class GoToOrganizationEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : GoToOrganizationEvent()
}
