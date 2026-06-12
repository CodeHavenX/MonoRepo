package com.cramsan.edifikana.client.lib.features.home.organizationhome

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Home feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class OrganizationHomeEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : OrganizationHomeEvent()
}
