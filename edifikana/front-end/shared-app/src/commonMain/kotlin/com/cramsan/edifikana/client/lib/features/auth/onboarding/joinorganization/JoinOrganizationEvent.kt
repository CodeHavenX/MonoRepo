package com.cramsan.edifikana.client.lib.features.auth.onboarding.joinorganization

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the JoinOrganization feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class JoinOrganizationEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : JoinOrganizationEvent()
}
