package com.cramsan.edifikana.client.lib.features.settings.organizations.myorganizations

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the MyOrganizations feature.
 */
sealed class MyOrganizationsEvent : ViewModelEvent {
    /** No operation. */
    data object Noop : MyOrganizationsEvent()
}
