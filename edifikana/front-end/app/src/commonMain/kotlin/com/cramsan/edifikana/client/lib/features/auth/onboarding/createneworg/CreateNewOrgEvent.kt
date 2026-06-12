package com.cramsan.edifikana.client.lib.features.auth.onboarding.createneworg

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the CreateNewOrg feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 */
sealed class CreateNewOrgEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : CreateNewOrgEvent()
}
